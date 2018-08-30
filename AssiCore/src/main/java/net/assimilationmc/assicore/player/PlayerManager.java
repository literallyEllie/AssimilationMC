package net.assimilationmc.assicore.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.event.PlayerJoinNetworkEvent;
import net.assimilationmc.assicore.friend.FriendData;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.patch.AssiPatch;
import net.assimilationmc.assicore.patch.PatchMinimap;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.RedisDatabaseIndex;
import net.assimilationmc.assicore.redis.RedisObjectHolder;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.reward.RewardType;
import net.assimilationmc.assicore.staff.StaffChatManager;
import net.assimilationmc.assicore.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerManager extends Module implements RedisObjectHolder {

    private static final String TABLE_PLAYERS = "assimc_players";
    private Map<UUID, AssiPlayer> onlinePlayers, offlinePlayerCache;
    private Gson gson = new Gson();

    private int ORIGINAL_PLAYER_COUNT;

    public PlayerManager(AssiPlugin plugin) {
        super(plugin, "Player Manager");
    }

    @Override
    protected void start() {
        this.onlinePlayers = Maps.newConcurrentMap();
        this.offlinePlayerCache = Maps.newHashMap();

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + TABLE_PLAYERS + "` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "`uuid` VARCHAR(100) NOT NULL UNIQUE, " +
                    "`name` VARCHAR(100) NOT NULL, " +
                    "`display_name` VARCHAR(100), " +
                    "`first_seen` BIGINT, " +
                    "`last_seen` BIGINT NOT NULL, " +
                    "`last_ip` VARCHAR(100) NOT NUll, " +
                    "`previous_names` MEDIUMTEXT NULL, " +
                    "`discord_account` BIGINT NULL, " +
                    "`rank` VARCHAR(100) NOT NULL," +
                    "`bucks` INT(100), " +
                    "`ultra_coins` INT(100), " +
                    "`joins` INT(100), " +
                    "`votes` INT(100), " +
                    "`vote_streak` INT(100), " +
                    "`last_vote` BIGINT NULL, " +
                    "`helpop_handled` INT(100), " +
                    "`referred_by` VARCHAR(100) NULL, " +
                    "`last_server` VARCHAR(100) NOT NULL, " +
                    "`friends` LONGTEXT NULL," +
                    "`friend_settings` TEXT NULL, " +
                    "`boosters` LONGTEXT NULL, " +
                    "`achievements` LONGTEXT NULL, " +
                    "`achievement_progress` LONGTEXT NULL, " +
                    "`cosmetics` LONGTEXT NULL, " +
                    "`last_reward_claim` LONGTEXT NULL, " +
                    "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to make opening statement to database!");
            e.printStackTrace();
        }

        ORIGINAL_PLAYER_COUNT = UtilServer.getMaxPlayers();

    }

    @Override
    protected void end() {
        // blocking
        onlinePlayers.values().forEach(assiPlayer -> {
            unloadPlayer(assiPlayer, false); // prevent CME
        });

        onlinePlayers.clear();
    }

    // Priority join
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(final AsyncPlayerPreLoginEvent e) {
        if (getPlugin().getServerData().isLocal()) return;

        UUID uuid = e.getUniqueId();
        Rank rank = null;

        if (!getPlugin().getServerData().isLocal()) {
            try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                if (jedis.exists(redisKey(uuid))) {
                    rank = Rank.fromString(jedis.hget(redisKey(uuid), "rank"));
                }
            }
        }

        if (rank == null) {

            try (Connection connection = getPlugin().getSqlManager().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT rank FROM `" + TABLE_PLAYERS + "` WHERE uuid = ?");
                preparedStatement.setString(1, uuid.toString());

                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    rank = Rank.fromString(resultSet.getString("rank"));
                }

                resultSet.close();
                preparedStatement.close();

            } catch (SQLException ex) {
                log(Level.WARNING, "Failed to do rank check for " + uuid + "!");
                ex.printStackTrace();
            }

        }
        if (rank == null) rank = Rank.PLAYER;

        if (UtilServer.isFull()) {

            if (!rank.isHigherThanOrEqualTo(Rank.DEMONIC)) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, C.II + "Looks like this server is full...\n\n" +
                        C.C + "Happen a-lot? Fear not, with ranks Demonic+ you can get access to full servers, never see this message again!\nSee " +
                        C.V + Domain.PROT_STORE);
                return;
            }

            for (AssiPlayer player : onlinePlayers.values()) {
                if (player.getRank().isDefault()) {

                    String reason = C.II + "Uhh, this is awkward...\nYou were removed to make space for another player." +
                            C.C + "Happen a-lot? Fear not, with ranks Demonic+ you can get access to full servers, Never see this message again!\nSee " +
                            C.V + Domain.PROT_STORE;

                    if (getPlugin().getServerData().isLobby()) {
                        player.getBase().kickPlayer(reason);
                        return;
                    } else {
                        sendLobby(player, reason);
                        return;
                    }

                }
            }

            UtilServer.setMaxPlayers(UtilServer.getOnlinePlayers() + 1);
            e.allow();

        } else {
            Rank requiredRank = getPlugin().getServerData().getRequiredRank();
            if (requiredRank == null) return;

            if (!rank.isHigherThanOrEqualTo(requiredRank)) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, C.II + ChatColor.BOLD.toString() + "You cannot join this server!\n" +
                        C.C + "You need at least " + requiredRank.getPrefix() + C.C + " to join");
            }

        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        AssiPlayer loadedPlayer = loadOnlinePlayer(player);
        if (loadedPlayer == null) return;

        for (AssiPatch assiPatch : getPlugin().getPatchManager().getPatchSet()) {
            if (assiPatch instanceof PatchMinimap) {
                ((PatchMinimap) assiPatch).onJoin(player);
            }
        }

        Rank requiredRank = getPlugin().getServerData().getRequiredRank();
        if (requiredRank != null) {
            loadedPlayer.sendMessage(C.C + "This is a restricted server. You need at least " + requiredRank.getPrefix() + C.C + " to join.");
        }

        if (!loadedPlayer.isVanished() && loadedPlayer.getRank().isHigherThanOrEqualTo(Rank.HELPER) && !getPlugin().getServerData().isDev()) {
            // check if game server
            loadedPlayer.setVanished(true);
        }

        if (!loadedPlayer.getRank().isHigherThanOrEqualTo(Rank.DEVELOPER)) {
            getPlugin().getPlayerManager().getOnlinePlayers().values().stream().filter(AssiPlayer::isVanished).forEach(assiPlayer -> loadedPlayer.getBase().hidePlayer(assiPlayer.getBase()));
        }

        if (loadedPlayer.getJoins() == 1) {
            UtilMessage.sendFullTitle(player, C.II + "Welcome to " + ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "milationMC",
                    C.II + "Your new central Hub for all things UHC!", 10, 50, 10);
        }

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(final PlayerQuitEvent e) {
        final AssiPlayer player = onlinePlayers.get(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> unloadPlayer(player, true));

        if (UtilServer.getMaxPlayers() > ORIGINAL_PLAYER_COUNT) {
            UtilServer.setMaxPlayers(UtilServer.getOnlinePlayers() - 1);
        }

    }

    /**
     * Attempts to load a player, this method should be run sync as we want to get their request ASAP.
     * Firstly, it will go to Redis, then if fail, MySQL, then if they're non-existent in both i
     * instances, it will just create a new player.
     *
     * @param player The player to load.
     * @return The loaded player.
     */
    private AssiPlayer loadOnlinePlayer(Player player) {
        if (onlinePlayers.containsKey(player.getUniqueId())) return onlinePlayers.get(player.getUniqueId());

        long start = System.currentTimeMillis();

        if (offlinePlayerCache.containsKey(player.getUniqueId())) {
            AssiPlayer assiPlayer = offlinePlayerCache.get(player.getUniqueId());
            getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                offlinePlayerCache.remove(player.getUniqueId());
                pushSql(assiPlayer);
                delRedis(assiPlayer);
            });
        }

        boolean sql = false;

        AssiPlayer assiPlayer = null;

        try {
//            D.d("checking redis player");
            assiPlayer = getPlayerRedis(player.getUniqueId());
        } catch (Exception e) {
            log(Level.SEVERE, "Failed to load " + player.getUniqueId() + " from redis!");
            e.printStackTrace();
        }

        if (assiPlayer == null) {
//            D.d("not in redis, going sql");
            try {
                assiPlayer = getPlayerSQL(player.getUniqueId());
            } catch (Exception e) {
                log(Level.SEVERE, "Failed to load player data of " + player.getUniqueId());

                if (getPlugin().getServerData().isLocal()) {
                    getPlugin().getDiscordCommunicator().messageChannel("BOT_LOGS",
                            "**Error** Failed to load player data `" + player.getUniqueId() + "` on " +
                                    getPlugin().getServerData().getId() + ": " + e.getMessage());
                }
                player.kickPlayer(C.II + "There was an error loading your data, to avoid data loss you have been kicked.\n" +
                        "Go to our Discord and ask for help " + C.V + Domain.DISCORD);
                e.printStackTrace();
                return null;
            }

            sql = true;

            if (assiPlayer == null) {
//                D.d("not in sql, creating");
                assiPlayer = createPlayer(player);

            } else pushRedis(assiPlayer);

            UtilServer.callEvent(new PlayerJoinNetworkEvent(assiPlayer));

        }

        if (assiPlayer != null && player.isOnline()) {
            onlinePlayers.put(player.getUniqueId(), assiPlayer);

            UtilMessage.sendTab(assiPlayer.getBase(), "\n" + C.C + "You're playing on " + ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "milationMC\n",
                    "\n" + C.V + getPlugin().getServerData().getId() + "\n" + C.C +
                            Domain.WEB + "\n" +
                            Domain.DISCORD + "\n");

            if (assiPlayer.getRank().isHigherThanOrEqualTo(Rank.HELPER) && sql) {
                getPlugin().getRedisManager().sendPubSubMessage(StaffChatManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.SPIGOT, getPlugin().getServerData().getId(),
                        "STAFF_JOIN", new String[]{player.getDisplayName()}));

                getPlugin().getStaffChatManager().msgStaffJoin(assiPlayer.getDisplayName());
            }

            AssiPlayer finalAssiPlayer = assiPlayer;
            getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> getPlugin().getPunishmentManager().requestPunishProfile(finalAssiPlayer));
        }

        D.d("Load online player block executed in " + (System.currentTimeMillis() - start) + "ms.");

        return assiPlayer;
    }

    /**
     * Unload a player, it will store their new request in Redis.
     *
     * @param player The player to unload.
     * @param rem    Should remove from onlinePlayers
     */
    private void unloadPlayer(AssiPlayer player, boolean rem) {
        if (getPlugin().getServerData().isLocal()) {
            pushSql(player);
            delRedis(player);
        } //else pushRedis(player); - all the request should be synchronized anyway
        if (rem) onlinePlayers.remove(player.getUuid());
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
    private AssiPlayer getPlayerRedis(UUID uuid) throws Exception {
        AssiPlayer player;

        if (getPlugin().getServerData().isLocal()) return null;

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);

            if (!jedis.exists(redisKey(uuid))) {
                return null;
            }

            // If this is true, the data is corrupt and it should be refreshed.
            if (jedis.hget(redisKey(uuid), "first_seen") == null) {
                return null;
            }

            String name;
            final List<String> previousNames = UtilJson.deserialize(gson, jedis.hget(redisKey(uuid), "previous_names"));
            long firstSeen;
            long lastSeen = System.currentTimeMillis();
            long discordAccount = 0;
            Rank rank;
            String[] ips;
            int bucks, ultraCoins, joins = 0, votes, voteStreak;
            long lastVote;
            String referredBy;
            int helopHandled;
            String lastSeenServer;
            List<CosmeticType> cosmeticTypes;
            FriendData friendData = new FriendData(uuid);
            Map<String, Integer> boosters;
            Map<String, Long> achievements;
            Map<String, String> achievementProgress;
            Map<RewardType, Long> lastRewardClaim;
            boolean verified = false;
            boolean vanished = false;

            Player onlinePlayer = UtilPlayer.get(uuid);
            final String key = redisKey(uuid);

            if (onlinePlayer != null) {
                name = onlinePlayer.getName();

                String storedName = jedis.hget(key, "name");

                if (storedName != null && !storedName.equals(onlinePlayer.getName())) {
                    jedis.set(onlinePlayer.getName().toLowerCase(), uuid.toString());
                    previousNames.add(storedName);
                }

                joins++;
                lastSeenServer = getPlugin().getServerData().getId();

            } else {
                name = jedis.hget(key, "name");
                lastSeen = Long.parseLong(jedis.hget(key, "last_seen"));
                lastSeenServer = String.valueOf(jedis.hget(key, "last_server"));
            }


            firstSeen = Long.valueOf(jedis.hget(key, "first_seen"));

            if (jedis.hexists(key, "discord")) {
                discordAccount = Long.valueOf(jedis.hget(key, "discord"));
            }
            rank = Rank.fromString(jedis.hget(key, "rank"));
            ips = new String[]{(onlinePlayer != null ? onlinePlayer.getAddress().getAddress().getHostAddress() : null),
                    jedis.hget(key, "last_ip")};
            bucks = Integer.parseInt(jedis.hget(key, "bucks"));
            ultraCoins = Integer.parseInt(jedis.hget(key, "ultra_coins"));
            joins = joins + Integer.parseInt(jedis.hget(key, "joins"));
            votes = Integer.parseInt(jedis.hget(key, "votes"));
            voteStreak = Integer.parseInt(jedis.hget(key, "vote_streak"));
            lastVote = Long.parseLong(jedis.hget(key, "last_vote"));
            helopHandled = Integer.parseInt(jedis.hget(key, "helpop_handled"));
            referredBy = jedis.hget(key, "referred_by");

            cosmeticTypes = UtilJson.deserialize(gson, new TypeToken<List<CosmeticType>>() {
            }, jedis.hget(key, "cosmetics"));

            friendData.setFriends(UtilJson.deserialize(gson, new TypeToken<Map<UUID, String>>() {
            }, jedis.hget(key, "friends")));
            friendData.setIncoming(UtilJson.deserialize(gson, new TypeToken<List<UUID>>() {
            }, jedis.hget(key, "friend_incoming")));
            friendData.deserializeSettings(jedis.hget(key, "friend_settings"));

            boosters = UtilJson.deserialize(gson, new TypeToken<Map<String, Integer>>() {
            }, jedis.hget(key, "boosters"));

            achievements = UtilJson.deserialize(gson, new TypeToken<Map<String, Long>>() {
            }, jedis.hget(key, "achievements"));
            achievementProgress = UtilJson.deserialize(gson, new TypeToken<Map<String, String>>() {
            }, jedis.hget(key, "achievement_progress"));

            lastRewardClaim = UtilJson.deserialize(gson, new TypeToken<Map<RewardType, Long>>() {
            }, jedis.hget(key, "last_reward_claim"));

            if (jedis.hexists(key, "verified")) {
                verified = Boolean.valueOf(jedis.hget(key, "verified"));
            }

            if (jedis.hexists(key, "vanished")) {
                vanished = Boolean.valueOf(jedis.hget(key, "vanished"));
            }

            player = new AssiPlayer(getPlugin(), uuid, name, previousNames, firstSeen, lastSeen, discordAccount, vanished, verified, rank, ips, bucks, ultraCoins, joins, votes,
                    voteStreak, lastVote, referredBy, helopHandled, cosmeticTypes, lastSeenServer, friendData, boosters, achievements, achievementProgress, lastRewardClaim);
            player.setBase(onlinePlayer);

        }

        return player;
    }

    /**
     * Attempts to load a player from MySQL.
     *
     * @param uuid The uuid of the player to load.
     * @return A player loaded from MySQL or null if they don't exist there.
     */
    private AssiPlayer getPlayerSQL(UUID uuid) throws Exception {

        AssiPlayer assiPlayer;

        Connection connection = getPlugin().getSqlManager().getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + TABLE_PLAYERS + "` WHERE uuid = ?");
        preparedStatement.setString(1, uuid.toString());
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) return null;

        final Player onlinePlayer = UtilPlayer.get(uuid);
        boolean updateName = false;

        String name;
        final List<String> previousNames = UtilJson.deserialize(gson, resultSet.getString("previous_names"));
        long firstSeen;
        long lastSeen = System.currentTimeMillis();
        long discordAccount;
        Rank rank;
        String[] ips;
        int bucks, ultraCoins, joins = 0, votes = 0, voteStreak = 0;
        long lastVote;
        String referredBy;
        int helpopHandled;
        List<CosmeticType> cosmeticTypes;
        String lastSeenServer;
        FriendData friendData = new FriendData(uuid);
        Map<String, Integer> boosters;
        Map<String, Long> achievements;
        Map<String, String> achievementProgress;
        Map<RewardType, Long> lastRewardClaim;

        if (onlinePlayer != null) {
            String storedName = resultSet.getString("name");
            updateName = !storedName.equals(onlinePlayer.getName());

            if (updateName) {
                previousNames.add(storedName);
            }

            name = onlinePlayer.getName();
            lastSeenServer = getPlugin().getServerData().getId();
            joins++;

        } else {

            name = resultSet.getString("name");
            lastSeen = resultSet.getLong("last_seen");
            lastSeenServer = resultSet.getString("last_server");

        }

        firstSeen = resultSet.getLong("first_seen");
        discordAccount = resultSet.getLong("discord_account");
        rank = Rank.fromString(resultSet.getString("rank"));

        ips = new String[]{(onlinePlayer != null ? onlinePlayer.getAddress().getAddress().getHostAddress() : null), resultSet.getString("last_ip")};
        bucks = resultSet.getInt("bucks");
        ultraCoins = resultSet.getInt("ultra_coins");
        joins = joins + resultSet.getInt("joins");
        votes = resultSet.getInt("votes");
        voteStreak = resultSet.getInt("vote_streak");
        lastVote = resultSet.getLong("last_vote");
        helpopHandled = resultSet.getInt("helpop_handled");
        referredBy = resultSet.getString("referred_by");

        friendData.setFriends(UtilJson.deserialize(gson, new TypeToken<Map<UUID, String>>() {
        }, resultSet.getString("friends")));
        friendData.deserializeSettings(resultSet.getString("friend_settings"));

        boosters = UtilJson.deserialize(gson, new TypeToken<Map<String, Integer>>() {
        }, resultSet.getString("boosters"));

        achievements = UtilJson.deserialize(gson, new TypeToken<Map<String, Long>>() {
        }, resultSet.getString("achievements"));
        achievementProgress = UtilJson.deserialize(gson, new TypeToken<Map<String, String>>() {
        }, resultSet.getString("achievement_progress"));

        cosmeticTypes = UtilJson.deserialize(gson, new TypeToken<List<CosmeticType>>() {
        }, resultSet.getString("cosmetics"));

        if (StringUtils.isNotEmpty(resultSet.getString("last_reward_claim"))) {
            lastRewardClaim = UtilJson.deserialize(gson, new TypeToken<Map<RewardType, Long>>() {
            }, resultSet.getString("last_reward_claim"));
        } else lastRewardClaim = Maps.newHashMap();

        preparedStatement.close();

        if (updateName) {
            preparedStatement = connection.prepareStatement("UPDATE `" + TABLE_PLAYERS + "` SET name = ? WHERE uuid = ?");
            preparedStatement.setString(1, onlinePlayer.getName());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        }

        connection.close();

        assiPlayer = new AssiPlayer(getPlugin(), uuid, name, previousNames, firstSeen, lastSeen, discordAccount, false, false, rank, ips, bucks, ultraCoins, joins,
                votes, voteStreak, lastVote, referredBy, helpopHandled, cosmeticTypes, lastSeenServer, friendData, boosters, achievements, achievementProgress, lastRewardClaim);
        assiPlayer.setBase(onlinePlayer);

        return assiPlayer;
    }

    /**
     * Create a player in Redis and then to MySQL.
     *
     * @param player The player to create it from.
     */
    private AssiPlayer createPlayer(Player player) {
        if (onlinePlayers.containsKey(player.getUniqueId())) return null;

        final AssiPlayer assiPlayer = new AssiPlayer(getPlugin(), player);
//        D.d("achievement progress: " + assiPlayer.getAchievementProgress());
//        D.d("friends: " + assiPlayer.getFriendData().getFriends());
        assiPlayer.setName(player.getName());
        assiPlayer.setPreviousNames(Lists.newArrayList(assiPlayer.getName()));
        assiPlayer.setFirstSeen(System.currentTimeMillis());
        assiPlayer.setLastSeen(System.currentTimeMillis());
        assiPlayer.setDiscordAccount(0);
        assiPlayer.setVerified(false);
        assiPlayer.setRank(Rank.PLAYER);
        assiPlayer.setIPs(new String[]{player.getAddress().getAddress().getHostAddress(), null});
        assiPlayer.setBucks(UtilServer.DEFAULT_BALANCE_BUCKS);
        assiPlayer.setUltraCoins(UtilServer.DEFAULT_BALANCE_UC);
        assiPlayer.setJoins(1);
        assiPlayer.setVotes(0);
        assiPlayer.setVoteStreak(0);
        assiPlayer.setLastVote(0);
        assiPlayer.setReferredBy("NONE");
        assiPlayer.setHelpopsHandled(0);
        assiPlayer.setLastSeenServer(getPlugin().getServerData().getId());

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + TABLE_PLAYERS +
                    "` (uuid, name, first_seen, last_seen, last_ip, previous_names, discord_account, rank, bucks, ultra_coins, joins, votes, vote_streak, " +
                    "last_vote, referred_by, helpop_handled, cosmetics, last_server, friends, friend_settings, boosters, achievements, achievement_progress, " +
                    "last_reward_claim) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setString(1, assiPlayer.getUuid().toString());
            preparedStatement.setString(2, assiPlayer.getName());
            preparedStatement.setLong(3, assiPlayer.getFirstSeen());
            preparedStatement.setLong(4, assiPlayer.getLastSeen());
            preparedStatement.setString(5, assiPlayer.lastIP());
            preparedStatement.setString(6, gson.toJson(assiPlayer.getPreviousNames()));
            preparedStatement.setLong(7, 0);
            preparedStatement.setString(8, assiPlayer.getRank().name());
            preparedStatement.setInt(9, assiPlayer.getBucks());
            preparedStatement.setInt(10, assiPlayer.getUltraCoins());
            preparedStatement.setInt(11, assiPlayer.getJoins());
            preparedStatement.setInt(12, assiPlayer.getVotes());
            preparedStatement.setInt(13, assiPlayer.getVoteStreak());
            preparedStatement.setLong(14, assiPlayer.getLastVote());
            preparedStatement.setString(15, assiPlayer.getReferredBy());
            preparedStatement.setInt(16, assiPlayer.getHelpopsHandled());
            preparedStatement.setString(17, gson.toJson(assiPlayer.getCosmeticTypes()));
            preparedStatement.setString(18, assiPlayer.getLastSeenServer());
            preparedStatement.setString(19, gson.toJson(assiPlayer.getFriendData().getFriends()));
            preparedStatement.setString(20, gson.toJson(assiPlayer.getFriendData().serializeSettings()));
            preparedStatement.setString(21, gson.toJson(assiPlayer.getBoosters()));
            preparedStatement.setString(22, gson.toJson(assiPlayer.getAchievements()));
            preparedStatement.setString(23, gson.toJson(assiPlayer.getAchievementProgress()));
            preparedStatement.setString(24, gson.toJson(assiPlayer.getLastRewardClaim()));

            preparedStatement.execute();

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to create player in MySQL!");
            e.printStackTrace();
        }

        onlinePlayers.put(player.getUniqueId(), assiPlayer);
        pushRedis(assiPlayer);

        return assiPlayer;
    }

    /**
     * Forcefully push all a player's data to the redis.
     *
     * @param player The player to push.
     */
    public void pushRedis(AssiPlayer player) {

        if (getPlugin().getServerData().isLocal()) return;

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);

            final String key = redisKey(player.getUuid());

            Transaction transaction = jedis.multi();
            transaction.hset(key, "name", player.getName());
            transaction.hset(key, "previous_names", gson.toJson(player.getPreviousNames()));
            transaction.hset(key, "first_seen", String.valueOf(player.getFirstSeen()));
            transaction.hset(key, "last_seen", String.valueOf(System.currentTimeMillis()));
            transaction.hset(key, "discord", String.valueOf(player.getDiscordAccount()));
            transaction.hset(key, "vanished", String.valueOf(player.isVanished()));
            transaction.hset(key, "rank", player.getRank().name());
            transaction.hset(key, "last_ip", player.getIPs()[0]);
            transaction.hset(key, "bucks", String.valueOf(player.getBucks()));
            transaction.hset(key, "ultra_coins", String.valueOf(player.getUltraCoins()));
            transaction.hset(key, "joins", String.valueOf(player.getJoins()));
            transaction.hset(key, "votes", String.valueOf(player.getVotes()));
            transaction.hset(key, "vote_streak", String.valueOf(player.getVoteStreak()));
            transaction.hset(key, "last_vote", String.valueOf(player.getLastVote()));
            transaction.hset(key, "referred_by", player.getReferredBy());
            transaction.hset(key, "helpop_handled", String.valueOf(player.getHelpopsHandled()));
            transaction.hset(key, "cosmetics", gson.toJson(player.getCosmeticTypes()));
            transaction.hset(key, "server", player.getLastSeenServer());
            transaction.hset(key, "friends", gson.toJson(player.getFriendData().getFriends()));
            transaction.hset(key, "friend_incoming", gson.toJson(player.getFriendData().getIncoming()));
            transaction.hset(key, "friend_settings", player.getFriendData().serializeSettings());
            transaction.hset(key, "boosters", gson.toJson(player.getBoosters()));
            transaction.hset(key, "achievements", gson.toJson(player.getAchievements()));
            transaction.hset(key, "achievement_progress", gson.toJson(player.getAchievementProgress()));
            transaction.hset(key, "last_reward_claim", gson.toJson(player.getLastRewardClaim()));

            transaction.set(player.getName().toLowerCase(), player.getUuid().toString());
            transaction.exec();

        }

    }

    /**
     * Unload a player from the Redis cache
     *
     * @param player The player to unload.
     */
    private void delRedis(AssiPlayer player) {
        if (getPlugin().getServerData().isLocal()) return;

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

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + TABLE_PLAYERS + "` SET first_seen = ?, " +
                    "last_seen = ?, last_ip = ?, previous_names = ?, discord_account = ?, rank = ?, bucks = ?, ultra_coins = ?, joins = ?, votes = ?, vote_streak = ?, " +
                    "last_vote = ?, referred_by = ?, helpop_handled = ?, cosmetics = ?, last_server = ?, friends = ?, friend_settings = ?, boosters = ?," +
                    " achievements = ?, achievement_progress = ?, last_reward_claim = ? WHERE uuid = ?");
            preparedStatement.setLong(1, player.getFirstSeen());
            preparedStatement.setLong(2, player.getLastSeen());
            preparedStatement.setString(3, (player.getIPs()[1] == null ? player.lastIP() : player.getIPs()[1])); // NEED TO HANDLE IF THEY WERE NEVER ONLINE IN FIRST PLACE
            preparedStatement.setString(4, gson.toJson(player.getPreviousNames()));
            preparedStatement.setLong(5, player.getDiscordAccount());
            preparedStatement.setString(6, player.getRank().name());
            preparedStatement.setInt(7, player.getBucks());
            preparedStatement.setInt(8, player.getUltraCoins());
            preparedStatement.setInt(9, player.getJoins());
            preparedStatement.setInt(10, player.getVotes());
            preparedStatement.setInt(11, player.getVoteStreak());
            preparedStatement.setLong(12, player.getLastVote());
            preparedStatement.setString(13, player.getReferredBy());
            preparedStatement.setInt(14, player.getHelpopsHandled());
            preparedStatement.setString(15, gson.toJson(player.getCosmeticTypes()));
            preparedStatement.setString(16, player.getLastSeenServer());
            preparedStatement.setString(17, gson.toJson(player.getFriendData().getFriends()));
            preparedStatement.setString(18, player.getFriendData().serializeSettings());
            preparedStatement.setString(19, gson.toJson(player.getBoosters()));
            preparedStatement.setString(20, gson.toJson(player.getAchievements()));
            preparedStatement.setString(21, gson.toJson(player.getAchievementProgress()));
            preparedStatement.setString(22, gson.toJson(player.getLastRewardClaim()));
            preparedStatement.setString(23, player.getUuid().toString());

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to push player request to SQL " + player.getUuid() + "!");
            e.printStackTrace();
        }

    }

    /**
     * Attempt to load an offline player.
     *
     * @param uuid the player UUID.
     * @return the player, null if they don't exist at all.
     */
    public AssiPlayer getOfflinePlayer(UUID uuid) {
        AssiPlayer attempt = onlinePlayers.get(uuid);
        if (attempt != null)
            return attempt;
        if (offlinePlayerCache.containsKey(uuid)) return offlinePlayerCache.get(uuid);

        try {
            attempt = getPlayerRedis(uuid);
        } catch (Exception e) {
        }

        if (attempt == null) {
            try {
                attempt = getPlayerSQL(uuid);
            } catch (Exception e) {
            }
        }

        if (attempt != null) {
            offlinePlayerCache.put(uuid, attempt);
            getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                if (!offlinePlayerCache.containsKey(uuid)) return;
                AssiPlayer assiPlayer = offlinePlayerCache.get(uuid);

//                if (assiPlayer.getPunishmentProfile(false) != null)
//                    getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> getPlugin().getPunishmentManager().unload(assiPlayer.getPunishProfile()));
                pushSql(assiPlayer);

                offlinePlayerCache.remove(uuid);
            }, 60 * 20);
        }

        return attempt;
    }

    /**
     * A method to tell you if the player is online, i.e loaded into the online player cache.
     *
     * @param uuid the uuid to check.
     * @return are they loaded to the onlinePlayers cache?
     */
    public boolean isOnline(UUID uuid) {
        return onlinePlayers.containsKey(uuid);
    }

    /**
     * Send a player to a lobby.
     *
     * @param player The player to send.
     * @param reason The reason to send to them as they disconnect.
     */
    public void sendLobby(AssiPlayer player, String reason) {
        Party party = getPlugin().getPartyManager().getPartyOf(player.getBase(), false);
        if (party != null && party.getLeader().equals(player.getUuid())) {
            getPlugin().getPartyManager().setTarget(party, "hub"); // just to stop disband
        }

        player.sendMessage(reason);
        getPlugin().getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.ALL, getPlugin().getServerData().getId(),
                "SEND_HUB", new String[]{player.getUuid().toString()}));
    }

    /**
     * Get the UUID from a name.
     *
     * @param name the name to find the UUID of
     * @return the UUID, or null if not possible.
     */
    public UUID getUUID(String name) {
        if (UtilPlayer.get(name) != null)
            return UtilPlayer.get(name).getUniqueId();

        UUID uuid = null;

        if (!getPlugin().getServerData().isLocal()) {
            try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                jedis.select(RedisDatabaseIndex.DATA_USERS);
                if (jedis.exists(name.toLowerCase()))
                    uuid = UUID.fromString(jedis.get(name.toLowerCase()));
            }
        }

        if (uuid == null) {
            try (Connection connection = getPlugin().getSqlManager().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `" + TABLE_PLAYERS + "` WHERE name = ?");
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    uuid = UUID.fromString(resultSet.getString("uuid"));
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                getPlugin().getLogger().warning("Failed to perform UUID lookup for " + name + "!");
                e.printStackTrace();
            }
        }

        return uuid;
    }

    /**
     * Attempts to send a message to send a message to a player. It isn't the most helpful method ever but enjoy yourself.
     *
     * @param uuid         The the uuid of the player to try and message
     * @param ignoreIfHere If the player is on this server, ignore them, yes or no.
     * @param message      the message to send to them if all conditions are met.
     */
    public void attemptGlobalPlayerMessage(UUID uuid, boolean ignoreIfHere, String message) {
        if (ignoreIfHere || UtilPlayer.get(uuid) != null) {
            UtilPlayer.get(uuid).sendMessage(message);
            return;
        }

        getPlugin().getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.PROXY, getPlugin().getServerData().getId(),
                "PLAYER_MESSAGE", new String[]{uuid.toString(), String.valueOf(ignoreIfHere), message}));
    }

    /**
     * Gets an online player without any attempt to get offline stats.
     *
     * @param player player to get stats of.
     * @return the AssiPlayer object mapped to said player.
     */
    public AssiPlayer getOnlinePlayer(Player player) {
        return onlinePlayers.get(player.getUniqueId());
    }

    /**
     * Get an online player by their UUID
     *
     * @param uuid The uuid of the specified player.
     * @return Either the AssiPlayer instance or null.
     */
    public AssiPlayer getPlayer(UUID uuid) {
        return getOfflinePlayer(uuid);
    }

    /**
     * Get an online player by their {@link Player} object.
     *
     * @param player The object of the player.
     * @return Either the AssiPlayer instance or null.
     */
    public AssiPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * @return An unmodifiable list of currently online players.
     */
    public Map<UUID, AssiPlayer> getOnlinePlayers() {
        return Collections.unmodifiableMap(onlinePlayers);
    }

    /**
     * @return all the cached offline players.
     */
    public Map<UUID, AssiPlayer> getOfflinePlayerCache() {
        return offlinePlayerCache;
    }

    /**
     * @return a GSON instance.
     */
    public Gson getGson() {
        return gson;
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
