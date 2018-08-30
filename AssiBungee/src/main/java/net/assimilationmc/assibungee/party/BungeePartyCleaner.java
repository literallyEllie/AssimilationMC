package net.assimilationmc.assibungee.party;

import com.google.gson.Gson;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.redis.RedisDatabaseIndex;
import net.assimilationmc.assibungee.redis.RedisObjectHolder;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilJson;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

public class BungeePartyCleaner extends Module implements RedisObjectHolder {

    private Gson gson;

    public BungeePartyCleaner(AssiBungee assiBungee) {
        super(assiBungee, "Party Cleaner");
    }

    @Override
    protected void start() {
        this.gson = new Gson();

    }

    @Override
    protected void end() {
    }

    @EventHandler
    public void on(final ServerConnectEvent e) {
        final ProxiedPlayer player = e.getPlayer();

        ProxyServer.getInstance().getScheduler().runAsync(getPlugin(), () -> {
            try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                jedis.select(RedisDatabaseIndex.PARTIES);
                String key = redisKey(player.getUniqueId());
                if (!jedis.exists(key)) return;

                final BungeeParty party = new BungeeParty(UUID.fromString(jedis.hget(key, "leader")));
                party.setTarget(jedis.hget(key, "target"));

                if (party.getLeader().equals(player.getUniqueId()) && party.getTarget() != null && party.getTarget().equals(e.getTarget().getName())) {
                    List<String> uuidStrings = UtilJson.deserialize(gson, jedis.hget(key, "members"));
                    uuidStrings.forEach(s -> party.getMembers().add(UUID.fromString(s)));

                    if (party.getTarget() == null || !ProxyServer.getInstance().getServersCopy().containsKey(party.getTarget()))
                        return;
                    // lets send the rest
                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(party.getTarget());
                    for (UUID uuid : party.getMembers()) {
                        ProxiedPlayer member = UtilPlayer.get(uuid);
                        if (member == null) continue;
                        if (member.getServer().getInfo() != null && !member.getServer().getInfo().getName().equals(party.getTarget())) {
                            member.connect(serverInfo);
                        }
                    }
                } else if (!party.getLeader().equals(player.getUniqueId())) {
                    e.setCancelled(true);
                    player.sendMessage(new ComponentBuilder("Hey! Sorry you can't change servers as a party member. Do ").color(C.II).append("/party leave")
                            .color(C.V).append(" to get your freedom back!").color(C.II).create());
                }

            }
        });

    }

    public void handleForceConnect(UUID uuid, ServerInfo to) {
        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.PARTIES);
            if (!jedis.exists(redisKey(uuid))) return;

            final BungeeParty party = new BungeeParty(UUID.fromString(jedis.hget(redisKey(uuid), "leader")));
            List<String> uuidStrings = UtilJson.deserialize(gson, jedis.hget(redisKey(uuid), "members"));
            uuidStrings.forEach(s -> party.getMembers().add(UUID.fromString(s)));

            if (uuid.equals(party.getLeader())) {
                party.setTarget(to.getName());
                updateParty(party);
                return;
            }

            party.getMembers().remove(uuid);
            UtilPlayer.get(uuid).sendMessage(new TextComponent(C.II + "You have been removed from your party."));
            updateParty(party);
        }

    }

    public void handleUnload(UUID uuid) {

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.PARTIES);
            if (!jedis.exists(redisKey(uuid))) return;

            BungeeParty party = new BungeeParty(UUID.fromString(jedis.hget(redisKey(uuid), "leader")));
            List<String> uuidStrings = UtilJson.deserialize(gson, jedis.hget(redisKey(uuid), "members"));
            uuidStrings.forEach(s -> party.getMembers().add(UUID.fromString(s)));

            if (party.getMembers().contains(uuid)) {
                party.getMembers().remove(uuid);
                jedis.del(redisKey(uuid));

                updateParty(party);
            } else if (party.getLeader().equals(uuid)) {
                jedis.del(redisKey(uuid));

                for (UUID member : party.getMembers()) {
                    jedis.del(redisKey(member));
                }
            }
        }

    }

    private void updateParty(BungeeParty party) {
        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.PARTIES);
            String key = redisKey(party.getLeader());
            final Transaction multi = jedis.multi();
            multi.hset(key, "leader", party.getLeader().toString());
            multi.hset(key, "members", gson.toJson(party.getMembers()));
            multi.hset(key, "target", party.getTarget());

            for (UUID uuid : party.getMembers()) {
                key = redisKey(uuid);
                multi.hset(key, "leader", party.getLeader().toString());
                multi.hset(key, "members", gson.toJson(party.getMembers()));
                multi.hset(key, "target", party.getTarget());
            }

            multi.exec();
        }
    }

    @Override
    public String getObjectPrefix() {
        return "party_";
    }

    @Override
    public String redisKey(Object object) {
        return getObjectPrefix() + object.toString();
    }
}
