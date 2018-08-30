package net.assimilationmc.assicore.party;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.redis.RedisDatabaseIndex;
import net.assimilationmc.assicore.redis.RedisObjectHolder;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilJson;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PartyManager extends Module implements RedisObjectHolder {

    public static final String PREFIX = ChatColor.AQUA + ChatColor.BOLD.toString() + "Party " + C.II;
    private Map<UUID, Party> parties;
    private int maxPlayers;
    private Gson gson;

    public PartyManager(AssiPlugin plugin) {
        super(plugin, "Party Manager");
    }

    @Override
    protected void start() {
        parties = Maps.newHashMap();
        this.maxPlayers = 5;
        this.gson = new Gson();

        getPlugin().getCommandManager().registerCommand(new CmdParty(this));
    }

    @Override
    protected void end() {
        parties.clear();
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        Party party = getPartyOf(e.getPlayer(), true);
        if (party == null) return;

        party.messageParty(PREFIX + C.V + party.formatName(e.getPlayer()) + C.II + " joined the server.");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final AsyncPlayerChatEvent e) {
        Party party = getPartyOf(e.getPlayer(), false);
        if (party == null) return;

        if (party.includes(e.getPlayer()) && party.getChatToggled().contains(e.getPlayer())) {
            party.chat(e.getPlayer(), e.getMessage());
            e.setCancelled(true);

            log("[PARTY CHAT OF " + party.getLeader() + "] " + e.getPlayer().getName() + ": " + e.getMessage());
        }

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        Party party = getPartyOf(e.getPlayer(), false);
        if (party == null) return;

        if (party.getLeader().equals(e.getPlayer().getUniqueId())) {
            disbandParty(party, !party.hasTarget());

        } else if (!party.hasTarget())
            leaveParty(e.getPlayer(), party);


    }

    public void createParty(Player leader) {
        if (parties.containsKey(leader.getUniqueId())) return;

        for (Party oParty : parties.values()) {
            oParty.getInvites().remove(leader.getUniqueId());
        }

        Party party = new Party(leader.getUniqueId());
        party.messageParty(PREFIX + "Welcome to your party " + C.V + leader.getName() + C.II + "!");
        leader.sendMessage(ChatColor.GREEN + "To talk in your Party chat do " + C.V + "/party chat");

        parties.put(leader.getUniqueId(), party);
        updatePartyRedis(party);
    }

    public void joinParty(Player who, Party party) {
        if (party.getMembers().size() == maxPlayers) {
            who.sendMessage(PartyManager.PREFIX + "This party is full.");
            party.uninvite(who);
            return;
        }

        party.addMember(who);
        party.messageParty(PREFIX + C.V + who.getName() + C.II + " has joined the party.");
        who.sendMessage(ChatColor.GREEN + "To talk in your Party chat do " + C.V + "/party chat");

        for (Party oParty : parties.values()) {
            oParty.getInvites().remove(who.getUniqueId());
        }

        UtilServer.callEvent(new PartyJoinEvent(party, getPlugin().getPlayerManager().getPlayer(who)));

        updatePartyRedis(party);
    }

    public void leaveParty(Player who, Party party) {
        if (party.getLeader().equals(who.getUniqueId())) {
            disbandParty(party, true);
            return;
        }
        party.removeMember(who);
        party.messageParty(PREFIX + C.V + who.getName() + " left the party.");
        who.sendMessage(PREFIX + C.II + "You have left the party.");

        if (getPlugin().getServerData().isLocal()) return;
        updatePartyRedis(party);

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.PARTIES);
            jedis.del(redisKey(who.getUniqueId()));
        }

    }

    public void disbandParty(Party party, boolean message) {
        if (message) {
            if (!getPlugin().getServerData().isLocal()) {
                try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                    jedis.select(RedisDatabaseIndex.PARTIES);
                    jedis.del(redisKey(party.getLeader()));
                    for (UUID uuid : party.getMembers()) {
                        jedis.del(redisKey(uuid));
                    }
                }
            }
            party.messageParty(PREFIX + "Your party has been disbanded.");
        }

        party.getInvites().clear();
        party.getMembers().clear();
        parties.remove(party.getLeader());
    }

    public void setTarget(Party party, String target) {
        if (getPlugin().getServerData().isLocal()) return;

        party.setTarget(target);
        updatePartyRedis(party);

    }

    public Party getPartyOf(Player player, boolean checkRedis) {
        if (parties.containsKey(player.getUniqueId()))
            return parties.get(player.getUniqueId());

        for (Party party : parties.values()) {
            if (party.includes(player))
                return party;
        }

        return checkRedis ? getRedisParty(player) : null;
    }

    private Party getRedisParty(Player player) {
        if (parties.containsKey(player.getUniqueId())) return parties.get(player.getUniqueId());

        if (getPlugin().getServerData().isLocal()) return null;

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.PARTIES);
            if (jedis.exists(redisKey(player.getUniqueId()))) {
                final String key = redisKey(player.getUniqueId());
                final UUID leader = UUID.fromString(jedis.hget(key, "leader"));
                final Party party = new Party(leader);
                List<String> uuidStrings = (UtilJson.deserialize(gson, jedis.hget(key, "members")));
                uuidStrings.forEach(s -> party.getMembers().add(UUID.fromString(s)));

                parties.put(leader, party);
                return party;
            }
        }

        return null;
    }

    private void updatePartyRedis(Party party) {
        if (getPlugin().getServerData().isLocal()) return;

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.PARTIES);
            String key = redisKey(party.getLeader());
            final Transaction multi = jedis.multi();
            multi.hset(key, "leader", party.getLeader().toString());
            multi.hset(key, "members", gson.toJson(party.getMembers()));
            if (party.hasTarget()) multi.hset(key, "target", party.getTarget());

            for (UUID uuid : party.getMembers()) {
                Player player = UtilPlayer.get(uuid);
                if (player == null) return;
                key = redisKey(player.getUniqueId());
                multi.hset(key, "leader", party.getLeader().toString());
                multi.hset(key, "members", gson.toJson(party.getMembers()));
                if (party.hasTarget()) multi.hset(key, "target", party.getTarget());
            }

            multi.exec();
        }
    }

    @Override
    public String getObjectPrefix() {
        return "party_";
    }

    @Override
    public String redisKey(Object leader) {
        return getObjectPrefix() + leader.toString();
    }

}
