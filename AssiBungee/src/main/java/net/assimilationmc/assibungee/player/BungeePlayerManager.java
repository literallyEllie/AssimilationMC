package net.assimilationmc.assibungee.player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.discord.DiscordPresetChannel;
import net.assimilationmc.assibungee.rank.Rank;
import net.assimilationmc.assibungee.redis.RedisDatabaseIndex;
import net.assimilationmc.assibungee.redis.RedisObjectHolder;
import net.assimilationmc.assibungee.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilJson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class BungeePlayerManager extends Module implements RedisObjectHolder {

    public static final String TABLE_PLAYERS = "assimc_players";
    private Gson gson = new Gson();

    private Map<String, Set<UUID>> ipWatcher;

    private File flatFileIPBan;
    private Set<String> ipBans;

    public BungeePlayerManager(AssiBungee plugin) {
        super(plugin, "Player Manager");
    }

    @Override
    protected void start() {
        this.ipWatcher = Maps.newHashMap();
        this.flatFileIPBan = new File(getPlugin().getDataFolder(), "ipbans.yml");

        if (!flatFileIPBan.exists()) {
            try {
                if (!flatFileIPBan.createNewFile()) throw new IOException();
            } catch (IOException e) {
                getPlugin().getLogger().severe("Failed to create " + flatFileIPBan.getName() + "!");
                e.printStackTrace();
                return;
            }
        }

        this.ipBans = Sets.newHashSet();

        try {
            Configuration configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(flatFileIPBan);

            ipBans.addAll(configuration.getStringList("banned"));

            if (getPlugin().getRedisManager() != null) {
                try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                    ipBans.forEach(s -> jedis.lpush("ip-bans", s));
                }
            }

        } catch (IOException e) {
            getPlugin().getLogger().warning("Failed to load ip-bans file!");
            e.printStackTrace();
        }

    }

    @Override
    protected void end() {
        ipWatcher.clear();
    }

    @EventHandler
    public void on(final PlayerHandshakeEvent e) {
        final PendingConnection pendingConnection = e.getConnection();
        if (ipBans.contains(pendingConnection.getAddress().getAddress().getHostAddress())) {
            e.getConnection().disconnect();
        }
    }

    @EventHandler
    public void on(final LoginEvent e) {
        final PendingConnection pendingConnection = e.getConnection();
        final String hostAddress = pendingConnection.getAddress().getAddress().getHostAddress();

        if (ipWatcher.containsKey(hostAddress)) {
            final Set<UUID> others = ipWatcher.get(hostAddress);

            // max 3 clients per ip
            if (others.size() >= 3) {
                e.setCancelled(true);
                e.setCancelReason(new ComponentBuilder("This IP is already in use by 3 other clients.").color(ChatColor.RED).create());
                return;
            }

            others.add(pendingConnection.getUniqueId());
            return;
        }

        ipWatcher.put(hostAddress, Sets.newHashSet(e.getConnection().getUniqueId()));
    }

    @EventHandler
    public void on(final PlayerDisconnectEvent e) {
        final String hostAddress = e.getPlayer().getAddress().getAddress().getHostAddress();

        if (ipWatcher.containsKey(hostAddress)) {
            final Set<UUID> others = ipWatcher.get(hostAddress);
            others.remove(e.getPlayer().getUniqueId());

            if (others.size() == 0) {
                ipWatcher.remove(hostAddress);
                return;
            }

            ipWatcher.replace(hostAddress, others);
        }

    }

    /**
     * Should be called when player disconnects from the network, or switches server
     * The player should be acquired from the internal listener for players coming upstream.
     * <p>
     * Data will be pushed to mysql, then deleted off redis if "unload" is true.
     *
     * @param player the player to push.
     * @param unload Should the player be deleted from the redis cache upon being updated.
     */
    public void pushPlayer(AssiPlayer player, boolean unload) {

        pushSql(player);
        if (unload) {
            try {
                if (player.getRank().isHigherThanOrEqualTo(Rank.HELPER)) {
                    getPlugin().getRedisManager().sendPubSubMessage("staffchat", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                            getPlugin().getServerData().getId(), "STAFF_LEAVE", new String[]{player.getDisplayName()}));
                    getPlugin().getPartyCleaner().handleUnload(player.getUuid());
                }

                if (player.getFriendData() != null && player.getFriendData().isSendJoinLeave() && !player.getFriendData().getFriends().isEmpty()) {
                    getPlugin().getRedisManager().sendPubSubMessage("FRIEND", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                            getPlugin().getServerData().getId(), "LEAVE", new String[]{player.getDisplayName(),
                            gson.toJson(player.getFriendData().getFriends().keySet())}));
                }

            } catch (Throwable e) {
                log(Level.WARNING, "Failed to perform additional tasks when unloading player " + player.getUuid() + "!");
                e.printStackTrace();
            } finally {
                delRedis(player);
            }
        }

    }

    /**
     * Go-to method to load a player.
     *
     * @param uuid The uuid of the player to load.
     * @return The player object from loading from Redis,
     * if the player is online, the request will be updated:
     * if not, it will just use the request in there.
     * The method can also return null.
     */
    public AssiPlayer getPlayerRedis(UUID uuid) {

        AssiPlayer assiPlayer = new AssiPlayer(uuid);
        String key = redisKey(uuid);

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);

            if (!jedis.exists(key))
                return null;

            assiPlayer.setName(jedis.hget(key, "name"));
            assiPlayer.setPreviousNames(UtilJson.deserialize(gson, jedis.hget(key, "previous_names")));
            assiPlayer.setFirstSeen(Long.parseLong(jedis.hget(key, "first_seen")));
            assiPlayer.setLastSeen(Long.parseLong(jedis.hget(key, "last_seen")));
            if (jedis.hexists(key, "discord")) {
                assiPlayer.setDiscordAccount(Long.valueOf(jedis.hget(key, "discord")));
            } else assiPlayer.setDiscordAccount(0);

            assiPlayer.setRank(Rank.fromString(jedis.hget(key, "rank")));
            assiPlayer.setIPs(new String[]{null, jedis.hget(key, "last_ip")});
            assiPlayer.setBucks(Integer.parseInt(jedis.hget(key, "bucks")));
            assiPlayer.setUltraCoins(Integer.parseInt(jedis.hget(key, "ultra_coins")));
            assiPlayer.setJoins(Integer.parseInt(jedis.hget(key, "joins")));
            assiPlayer.setVotes(Integer.parseInt(jedis.hget(key, "votes")));
            assiPlayer.setVoteStreak(Integer.parseInt(jedis.hget(key, "vote_streak")));
            assiPlayer.setLastVote(Long.parseLong(jedis.hget(key, "last_vote")));
            assiPlayer.setReferredBy(jedis.hget(key, "referred_by"));
            assiPlayer.setHelpopsHandled(Integer.parseInt(jedis.hget(key, "helpop_handled")));
            assiPlayer.setLastSeenServer(jedis.hget(key, "server"));

            final FriendData friendData = assiPlayer.getFriendData();
            friendData.setFriends(UtilJson.deserialize(gson, new TypeToken<Map<UUID, String>>() {
            }, jedis.hget(key, "friends")));
            friendData.setIncoming(UtilJson.deserialize(gson, new TypeToken<List<UUID>>() {
            }, jedis.hget(key, "friend_incoming")));
            friendData.deserializeSettings(jedis.hget(key, "friend_settings"));

            assiPlayer.setBoosters(UtilJson.deserialize(gson, new TypeToken<Map<String, Integer>>() {
            }, jedis.hget(key, "boosters")));

            assiPlayer.setAchievements(UtilJson.deserialize(gson, new TypeToken<Map<String, Long>>() {
            }, jedis.hget(key, "achievements")));
            assiPlayer.setAchievementProgress(UtilJson.deserialize(gson, new TypeToken<Map<String, String>>() {
            }, jedis.hget(key, "achievement_progress")));

            assiPlayer.setLastRewardClaim(UtilJson.deserialize(gson, new TypeToken<Map<String, Long>>() {
            }, jedis.hget(key, "last_reward_claim")));

            assiPlayer.resetName();
        }

        return assiPlayer;
    }

    /**
     * Unload a player from the Redis cache
     *
     * @param player The player to unload.
     */
    private void delRedis(AssiPlayer player) {
        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);
            jedis.del(redisKey(player.getUuid()));
            jedis.del(player.getName().toLowerCase());
        }
    }

    /**
     * Forcefully push all the player's request to the MySQL.
     *
     * @param player The player to push request to.
     */
    public void pushSql(AssiPlayer player) {

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + TABLE_PLAYERS + "` SET " +
                    "last_seen = ?, last_ip = ?, previous_names = ?, discord_account = ?, rank = ?, bucks = ?, ultra_coins = ?, joins = ?, votes = ?, vote_streak = ?, " +
                    "last_vote = ?, referred_by = ?, helpop_handled = ?, last_server = ?, friends = ?, friend_settings = ?, boosters = ?, achievements = ?," +
                    " achievement_progress = ?, last_reward_claim = ? WHERE uuid = ?");
            preparedStatement.setLong(1, player.getLastSeen());
            preparedStatement.setString(2, (player.getIPs()[1] == null ? player.lastIP() : player.getIPs()[1])); // NEED TO HANDLE IF THEY WERE NEVER ONLINE IN FIRST PLACE
            preparedStatement.setString(3, gson.toJson(player.getPreviousNames()));
            preparedStatement.setLong(4, player.getDiscordAccount());
            preparedStatement.setString(5, player.getRank().name());
            preparedStatement.setInt(6, player.getBucks());
            preparedStatement.setInt(7, player.getUltraCoins());
            preparedStatement.setInt(8, player.getJoins());
            preparedStatement.setInt(9, player.getVotes());
            preparedStatement.setInt(10, player.getVoteStreak());
            preparedStatement.setLong(11, player.getLastVote());
            preparedStatement.setString(12, player.getReferredBy());
            preparedStatement.setInt(13, player.getHelpopsHandled());
            preparedStatement.setString(14, player.getLastSeenServer());
            preparedStatement.setString(15, gson.toJson(player.getFriendData().getFriends()));
            preparedStatement.setString(16, player.getFriendData().serializeSettings());
            preparedStatement.setString(17, gson.toJson(player.getBoosters()));
            preparedStatement.setString(18, gson.toJson(player.getAchievements()));
            preparedStatement.setString(19, gson.toJson(player.getAchievementProgress()));
            preparedStatement.setString(20, gson.toJson(player.getLastRewardClaim()));
            preparedStatement.setString(21, player.getUuid().toString());

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to push player request to SQL " + player.getUuid() + "!");
            e.printStackTrace();
        }

    }

    public UUID getUuidSql(String name) {
        UUID uuid = null;

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM `" + TABLE_PLAYERS + "` WHERE name = ?");
            preparedStatement.setString(1, name);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                uuid = UUID.fromString(resultSet.getString("uuid"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed get uuid of " + name + "!");
            e.printStackTrace();
        }

        return uuid;
    }

    public void ipBan(String ip, String dispatcher) {

        boolean remove = ipBans.contains(ip);

        if (remove) {
            getPlugin().getDiscordManager().messageChannel(DiscordPresetChannel.PUNISH_LOG, "Override un-IP ban executed by " + dispatcher + " for " + ip);
            ipBans.remove(ip);
        } else {
            for (ProxiedPlayer player : getPlugin().getProxy().getPlayers()) {
                if (player.getAddress().getAddress().getHostAddress().equals(ip)) {
                    player.disconnect(new ComponentBuilder(C.II + "IP Blacklisted").bold(true).create());
                }
            }
            getPlugin().getDiscordManager().messageChannel(DiscordPresetChannel.PUNISH_LOG, "Override IP ban executed by " + dispatcher + " for " + ip);
            ipBans.add(ip);
        }

        try {
            Configuration configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(flatFileIPBan);
            configuration.set("banned", ipBans);
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, flatFileIPBan);

            if (getPlugin().getRedisManager() != null) {
                try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                    if (remove) {
                        jedis.lrem("ip-bans", 0, ip);
                    } else jedis.lpush("ip-bans", ip);
                }
            }

        } catch (IOException e) {
            getPlugin().getLogger().warning("Failed to load/save ip bans file!");
            e.printStackTrace();
        }
    }

    /**
     * @return a GSON instance.
     */
    public Gson getGson() {
        return gson;
    }

    public Map<String, Set<UUID>> getIpWatcher() {
        return ipWatcher;
    }

    @Override
    public String getObjectPrefix() {
        return "player_";
    }

    @Override
    public String redisKey(Object object) {
        return getObjectPrefix() + object.toString();
    }
}
