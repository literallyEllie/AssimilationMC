package net.assimilationmc.assicore.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.friend.FriendData;
import net.assimilationmc.assicore.punish.PunishProfile;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.RedisDatabaseIndex;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.reward.RewardType;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AssiPlayer implements CommandSender {

    private final AssiPlugin plugin;

    private final UUID uuid;
    private Player base;
    private String name;
    private List<String> previousNames;

    private String displayName;
    private long firstSeen, lastSeen, discordAccount;
    private boolean vanished, verified;

    private Rank rank;
    private String[] ips;

    private int bucks, ultraCoins;
    private int joins, votes, voteStreak;
    private long lastVote;
    private String referredBy;
    private int helpopsHandled;
    private List<CosmeticType> cosmeticTypes;

    private String lastSeenServer; // can also be their current

    private PunishProfile punishProfile;
    private FriendData friendData;
    private Map<String, Integer> boosters;
    private Map<String, Long> achievements;
    private Map<String, String> achievementProgress;
    private Map<RewardType, Long> lastRewardClaim;

    private ChatColor overrideChatColor;

    public AssiPlayer(AssiPlugin plugin, UUID uuid, String name, List<String> previousNames, long firstSeen, long lastSeen, long discordAccount, boolean vanished, boolean verified,
                      Rank rank, String[] ips, int bucks, int ultraCoins, int joins, int votes, int voteStreak, long lastVote, String referredBy, int helpopsHandled, List<CosmeticType> cosmeticTypes,
                      String lastSeenServer, FriendData friendData, Map<String, Integer> boosters, Map<String, Long> achievements, Map<String, String> achievementProgress,
                      Map<RewardType, Long> lastRewardClaim) {
        this(plugin, uuid);
        this.name = name;
        this.previousNames = previousNames;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.discordAccount = discordAccount;
        this.vanished = vanished;
        this.verified = verified;
        this.rank = rank;
        this.ips = ips;
        this.bucks = bucks;
        this.ultraCoins = ultraCoins;
        this.joins = joins;
        this.votes = votes;
        this.voteStreak = voteStreak;
        this.lastVote = lastVote;
        this.referredBy = referredBy;
        this.helpopsHandled = helpopsHandled;
        this.lastSeenServer = lastSeenServer;
        this.cosmeticTypes = cosmeticTypes;
        resetName();
        this.friendData = friendData;
        friendData.setPlayer(this);
        this.boosters = boosters;
        this.achievements = achievements;
        this.achievementProgress = achievementProgress;
        this.lastRewardClaim = lastRewardClaim;
    }

    /**
     * Custom Player wrapper sort of thing.
     * Contains unique information about a player
     *
     * @param player Player to base it around
     */
    AssiPlayer(AssiPlugin plugin, Player player) {
        this(plugin, player.getUniqueId());
        this.base = player;
        this.name = player.getName();
        this.displayName = player.getDisplayName();
        this.lastSeen = player.getLastPlayed();
        this.previousNames = Lists.newArrayList();
        this.cosmeticTypes = Lists.newArrayList();
        this.lastSeenServer = plugin.getServerData().getId();
        this.friendData = new FriendData(uuid);
        this.friendData.setPlayer(this);
        this.boosters = Maps.newHashMap();
        this.achievements = Maps.newHashMap();
        this.achievementProgress = Maps.newHashMap();
        this.lastRewardClaim = Maps.newHashMap();
    }

    /**
     * @param uuid The UUID to base it around
     */
    public AssiPlayer(AssiPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    public AssiPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get player UUID
     *
     * @return player's UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * If not null, return the base of the player
     *
     * @return Underlying player object.
     */
    public Player getBase() {
        if (base == null) {
            base = UtilPlayer.get(uuid);
        }
        return base;
    }

    /**
     * Set the player base
     *
     * @param base Underlying player object.
     */
    public void setBase(Player base) {
        this.base = base;
    }

    /**
     * Get the claimed name of the player
     *
     * @return Name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Set the player name
     *
     * @param name Name of the player
     */
    public void setName(String name) {
        this.name = name;
        if (getPlugin().getServerData().isLocal()) return;
        if (!name.equals("CONSOLE")) {
            updateRedis("name", name);
            try (Jedis jedis = plugin.getRedisManager().getPool().getResource()) {
                jedis.select(RedisDatabaseIndex.DATA_USERS);
                jedis.set(name.toLowerCase(), uuid.toString());
            }
        }
    }

    /**
     * @return A list of the user's previous names.
     */
    public List<String> getPreviousNames() {
        return previousNames;
    }

    /**
     * Set the user's previous names.
     *
     * @param previousNames A list of their previous names.
     */
    public void setPreviousNames(List<String> previousNames) {
        this.previousNames = previousNames;
        updateRedis("previous_names", plugin.getPlayerManager().getGson().toJson(previousNames));
    }

    /**
     * Get the display name
     * This should be used in the chat and most things
     *
     * @return The display name of the player.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the the display name of the player and update it for the underlying player object
     * if not null
     *
     * @param displayName display name to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (isOnline()) {
            base.setDisplayName(displayName);
        }
    }

    /**
     * Resets their display name, used when updating ranks and whatnot.
     */
    public void resetName() {
        setDisplayName(rank.getPrefix() + (rank.isDefault() ? "" : " ") + name);
    }

    /**
     * @return the timestamp in which the player was first seen on the network.
     */
    public long getFirstSeen() {
        return firstSeen;
    }

    /**
     * Set the timestamp in which the player was first seen.
     *
     * @param firstSeen the timestamp.
     */
    public void setFirstSeen(long firstSeen) {
        this.firstSeen = firstSeen;
        updateRedis("first_seen", String.valueOf(firstSeen));
    }

    /**
     * Get last seen of player
     *
     * @return Their last seen or if they're online return {@link System#currentTimeMillis()}
     */
    public long getLastSeen() {
        return (isOnline() ? System.currentTimeMillis() : lastSeen);
    }

    /**
     * Set the last seen of a player
     *
     * @param lastSeen Last seen
     */
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
        updateRedis("last_seen", String.valueOf(lastSeen));
    }

    /**
     * Gets the players verified Discord account
     *
     * @return If they have one, the Discord user ID, if not, 0
     */
    public long getDiscordAccount() {
        return discordAccount;
    }

    /**
     * Set their discord user id
     *
     * @param discordAccount user id
     */
    public void setDiscordAccount(long discordAccount) {
        this.discordAccount = discordAccount;
        updateRedis("discord", String.valueOf(discordAccount));
    }

    /**
     * Get player rank
     *
     * @return Player's rank
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Set the player's rank
     *
     * @param rank Rank to set
     */
    public void setRank(Rank rank) {
        this.rank = rank;
        updateRedis("rank", rank.name());
        if (getBase() != null && !rank.isDefault())
            getBase().sendMessage(C.II + "Your rank has been updated to " + rank.getPrefix() + C.II + ".");
        resetName();
    }

    /**
     * @return is the player vanished.
     */
    public boolean isVanished() {
        return vanished;
    }

    /**
     * Set the player equal to a value of vanished.
     *
     * @param vanished are they vanished or not.
     */
    public void setVanished(boolean vanished) {
        this.vanished = vanished;
        if (getBase() != null) {
            getBase().sendMessage(C.II + ChatColor.BOLD + "You have been " + C.V + ChatColor.BOLD +
                    (vanished ? "vanished" : "unvanished") + C.II + ChatColor.BOLD + ".");

            if (vanished) {
                plugin.getPlayerManager().getOnlinePlayers().values()
                        .stream().filter(assiPlayer -> !assiPlayer.getRank().isHigherThanOrEqualTo(Rank.DEVELOPER))
                        .forEach(assiPlayer -> assiPlayer.getBase().hidePlayer(getBase()));
            } else
                Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(getBase()));

        }
        updateRedis("vanished", String.valueOf(vanished));
    }

    /**
     * @return if they're a user which requires 2FA, have they completed it correctly?
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Set a user to verified.
     *
     * @param verified are they verified or not.
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
        updateRedis("verified", String.valueOf(verified));
    }

    /**
     * @return How many bucks they have.
     */
    public int getBucks() {
        return bucks;
    }

    /**
     * Set their bucks
     *
     * @param bucks The amount of bucks they are to have.
     */
    public void setBucks(int bucks) {
        if (bucks < 0) bucks = 0;
        this.bucks = bucks;
        updateRedis("bucks", String.valueOf(bucks));
        sendMessage(C.C + "New Bucks balance: " + C.BUCKS + bucks + "B");
    }

    /**
     * Add some bucks.
     *
     * @param bucks The amount of bucks to add.
     */
    public void addBucks(int bucks) {
        if (getBase() != null) base.sendMessage(ChatColor.DARK_GREEN + "+" + C.BUCKS + bucks + "B");
        setBucks(this.bucks + Math.abs(bucks));
    }

    /**
     * Take some bucks.
     *
     * @param bucks The amount of bucks to add.
     */
    public void takeBucks(int bucks) {
        if (getBase() != null) base.sendMessage(ChatColor.RED + "-" + C.BUCKS + bucks + "B");
        setBucks(this.bucks - bucks);
    }

    /**
     * Check if a player can afford a certain amount in bucks.
     *
     * @param bucks the transaction amount.
     * @return If their balance - transaction is greater than or equal to 0.
     */
    public boolean canAffordBucks(int bucks) {
        return this.bucks - bucks >= 0;
    }

    /**
     * @return How much ultra coins they have. (More premium)
     */
    public int getUltraCoins() {
        return ultraCoins;
    }

    /**
     * Set their ultra coins.
     *
     * @param ultraCoins The amount of ultra coins they are to have.
     */
    public void setUltraCoins(int ultraCoins) {
        if (ultraCoins < 0) ultraCoins = 0;
        if (getBase() != null) base.sendMessage(C.C + "New Ultra Coins balance: " + C.UC + ultraCoins + "UC");
        this.ultraCoins = ultraCoins;
        updateRedis("ultra_coins", String.valueOf(ultraCoins));
    }

    /**
     * Add some ultra coins.
     *
     * @param ultraCoins The amount of ultra coins to add.
     */
    public void addUltraCoins(int ultraCoins) {
        if (getBase() != null) base.sendMessage(ChatColor.DARK_GREEN + "+" + C.UC + ultraCoins + "UC");
        setUltraCoins(this.ultraCoins + ultraCoins);
    }

    /**
     * Take some bucks.
     *
     * @param ultraCoins The amount of bucks to take.
     */
    public void takeUltraCoins(int ultraCoins) {
        if (getBase() != null) base.sendMessage(ChatColor.RED + "-" + C.UC + ultraCoins + "UC");
        setUltraCoins(this.ultraCoins - ultraCoins);
    }

    /**
     * Check if a player can afford a certain amount in Ultra Coins.
     *
     * @param ultraCoins the transaction amount.
     * @return If their balance - transaction is greater than or equal to 0.
     */
    public boolean canAffordUltraCoins(int ultraCoins) {
        return this.ultraCoins - ultraCoins >= 0;
    }

    /**
     * Get player's join count.
     *
     * @return Player's join count.
     */
    public int getJoins() {
        return joins;
    }

    /**
     * Set player's join count
     *
     * @param joins Join count
     */
    public void setJoins(int joins) {
        this.joins = joins;
        updateRedis("joins", String.valueOf(joins));
    }

    /**
     * Increment joins of player
     */
    public void addJoin() {
        setJoins(joins + 1);
    }

    /**
     * @return the votes of a player.
     */
    public int getVotes() {
        return votes;
    }

    /**
     * Set the votes of a player.
     *
     * @param votes the new vote count.
     */
    public void setVotes(int votes) {
        this.votes = votes;
        updateRedis("votes", String.valueOf(votes));
    }

    /**
     * Increment the votes of a player.
     */
    public void addVote() {
        setVotes(votes + 1);
    }

    /**
     * @return the vote streak of the player.
     */
    public int getVoteStreak() {
        return voteStreak;
    }

    /**
     * Set their vote streak.
     *
     * @param voteStreak the new vote streak count.
     */
    public void setVoteStreak(int voteStreak) {
        this.voteStreak = voteStreak;
        updateRedis("vote_streak", String.valueOf(voteStreak));
    }

    public boolean hasVoteStreak() {
        return voteStreak > 0;
    }

    /**
     * Increment their vote count by 1.
     */
    public void addVoteStreak() {
        setVoteStreak(voteStreak + 1);
    }

    /**
     * @return the last time they voted.
     */
    public long getLastVote() {
        return lastVote;
    }

    /**
     * Set the last time they voted.
     *
     * @param lastVote the last time they voted.
     */
    public void setLastVote(long lastVote) {
        this.lastVote = lastVote;
        updateRedis("last_vote", String.valueOf(lastVote));
    }

    /***
     *  Returns their current and last IP.
     * @return String[0] = Current IP, may be null if they're offline
     *          String[1] = Last IP, may be null if this is their first join.
     */
    public String[] getIPs() {
        return ips;
    }

    /**
     * Set the ips
     *
     * @param ips String array of IPs.
     */
    public void setIPs(String[] ips) {
        this.ips = ips;
        updateRedis("last_ip", lastIP());
    }

    /**
     * Get their last IP
     *
     * @return Return their last IP if they're offline, or their current IP if they're online.
     */
    public String lastIP() {
        return ips[0] == null ? ips[1] : ips[0];
    }

    /**
     * @return the player who they were referred by.
     * If it returns "NONE" they have not been referred by another.
     */
    public String getReferredBy() {
        return referredBy;
    }

    /**
     * Set the player whom they were refereed by.
     *
     * @param referredBy The person they were referred by.
     */
    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
        updateRedis("referred_by", referredBy);
    }

    /**
     * @return the amount of HelpOPs the players have handled.
     * For majority of players this will always be 0.
     */
    public int getHelpopsHandled() {
        return helpopsHandled;
    }

    /**
     * Set how many HelpOPs have been handled by a player.
     *
     * @param helpopsHandled the amount of HelpOPs handled.
     */
    public void setHelpopsHandled(int helpopsHandled) {
        this.helpopsHandled = helpopsHandled;
        updateRedis("helpop_handled", String.valueOf(helpopsHandled));
    }

    /**
     * Increment the HelpOP handled counter by 1.
     */
    public void addHelpopHandle() {
        setHelpopsHandled(helpopsHandled + 1);
    }

    /**
     * @return all the cosmetic they have UNLOCKED.
     * This will not include cosmetics by rank.
     */
    public List<CosmeticType> getCosmeticTypes() {
        return cosmeticTypes;
    }

    /**
     * Set the unlocked cosmetics of a player.
     *
     * @param cosmeticTypes a list of unlocked cosmetics;
     */
    public void setCosmeticTypes(List<CosmeticType> cosmeticTypes) {
        this.cosmeticTypes = cosmeticTypes;
        updateRedis("cosmetics", plugin.getPlayerManager().getGson().toJson(cosmeticTypes));
    }

    public void addCosmetic(CosmeticType cosmeticType) {
        if (cosmeticTypes.contains(cosmeticType)) return;
        cosmeticTypes.add(cosmeticType);
    }

    public void removeCosmetic(CosmeticType cosmeticType) {
        cosmeticTypes.remove(cosmeticType);
    }

    public boolean hasCosmetic(CosmeticType cosmeticType) {
        return cosmeticTypes.contains(cosmeticType)
                || plugin.getCosmeticManager().getCosmeticsForRank(rank).contains(cosmeticType);
    }

    /**
     * @return the player's last seen server, if they're online,
     * it will be that.
     */
    public String getLastSeenServer() {
        return lastSeenServer;
    }

    /**
     * Set the player's last seen server, if they're moving, it is THIS server.
     *
     * @param lastSeenServer the server they're on
     */
    public void setLastSeenServer(String lastSeenServer) {
        this.lastSeenServer = lastSeenServer;
        updateRedis("server", lastSeenServer);
    }

    public PunishProfile getPunishmentProfile(boolean requestIfNull) {
        if (punishProfile == null && requestIfNull) {
            plugin.getServer().getScheduler().runTaskAsynchronously(getPlugin(), () ->
                    plugin.getPunishmentManager().requestPunishProfile(this));
        }
        return punishProfile;
    }

    public PunishProfile getPunishProfile() {
        return getPunishmentProfile(true);
    }

    public void setPunishProfile(PunishProfile punishProfile) {
        this.punishProfile = punishProfile;
    }

    public FriendData getFriendData() {
        return friendData;
    }

    public void setFriendData(FriendData friendData) {
        this.friendData = friendData;
    }

    public Map<String, Integer> getBoosters() {
        return boosters;
    }

    public void setBoosters(Map<String, Integer> boosters) {
        this.boosters = boosters;
        updateRedis("boosters", getPlugin().getPlayerManager().getGson().toJson(boosters));
    }

    public void addBooster(String id) {
        boosters.put(id.toLowerCase(), boosters.getOrDefault(id.toLowerCase(), 0) + 1);
        updateRedis("boosters", getPlugin().getPlayerManager().getGson().toJson(boosters));
    }

    public void removeBooster(String id) {
        int a = boosters.getOrDefault(id.toLowerCase(), 0);
        if (a == 0) return;

        if (a - 1 == 0) {
            boosters.remove(id.toLowerCase());
        } else boosters.put(id.toLowerCase(), a - 1);

        updateRedis("boosters", getPlugin().getPlayerManager().getGson().toJson(boosters));
    }

    public Map<String, Long> getAchievements() {
        return achievements;
    }

    public void setAchievements(Map<String, Long> achievements) {
        this.achievements = achievements;
    }

    public boolean hasAchievement(String id) {
        return achievements.containsKey(id.toUpperCase());
    }

    public void addAchievement(String id) {
        this.achievements.put(id.toUpperCase(), UtilTime.now());
        updateRedis("achievements", getPlugin().getPlayerManager().getGson().toJson(achievements));
    }

    public Map<String, String> getAchievementProgress() {
        return achievementProgress;
    }

    public void setAchievementProgress(Map<String, String> achievementProgress) {
        this.achievementProgress = achievementProgress;
        updateRedis("achievement_progress", getPlugin().getPlayerManager().getGson().toJson(achievementProgress));
    }

    public String getProgressOrDefault(String achievementId, String defaultValue) {
        return this.achievementProgress.getOrDefault(achievementId, defaultValue);
    }

    public void editProgress(String achievementId, String value) {
        this.achievementProgress.put(achievementId, value);
        updateRedis("achievement_progress", getPlugin().getPlayerManager().getGson().toJson(achievementProgress));
    }

    public void clearProgress(String achievementId) {
        if (hasStartedAchievement(achievementId)) {
            this.achievementProgress.remove(achievementId);
            updateRedis("achievement_progress", getPlugin().getPlayerManager().getGson().toJson(achievementProgress));
        }
    }

    public boolean hasStartedAchievement(String achievementId) {
        return this.achievementProgress.containsKey(achievementId);
    }

    public Map<RewardType, Long> getLastRewardClaim() {
        return lastRewardClaim;
    }

    public void setLastRewardClaim(Map<RewardType, Long> lastRewardClaim) {
        this.lastRewardClaim = lastRewardClaim;
        updateRedis("last_reward_claim", getPlugin().getPlayerManager().getGson().toJson(lastRewardClaim));
    }

    public void setRewardClaimed(RewardType rewardClaimed) {
        lastRewardClaim.put(rewardClaimed, UtilTime.now());
        setLastRewardClaim(this.lastRewardClaim);
    }

    public ChatColor getOverrideChatColor() {
        return overrideChatColor;
    }

    public void setOverrideChatColor(ChatColor overrideChatColor) {
        this.overrideChatColor = overrideChatColor;
    }

    /**
     * Returns if player is online
     *
     * @return the underlying player base isn't null and the base is online.
     */
    public boolean isOnline() {
        if (base == null) {
            Player online = UtilPlayer.get(uuid);
            if (online != null)
                setBase(online);
        }
        return base != null && base.isOnline();
    }

    public void updateRedis(String key, String value) {
        if (getName().equals("CONSOLE")) return;
        if (getPlugin().getServerData().isLocal()) return;
        if (getBase() != null) {
            try (Jedis jedis = plugin.getRedisManager().getPool().getResource()) {
                jedis.select(RedisDatabaseIndex.DATA_USERS);
                jedis.hset(plugin.getPlayerManager().redisKey(uuid), key, value);
            }
            return;
        }

        getPlugin().getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.SPIGOT, getPlugin().getServerData().getId(),
                "UPDATE_PLAYER", new String[]{uuid.toString(), String.valueOf(key), value}));
    }

    /*
     * BELOW IS IMPLEMENTED METHODS OF CommandSender
     */

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return base.getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(String name) {
        return getBase() != null && base.isOp();
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return getBase() != null && base.isOp();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return getBase() != null && base.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return getBase() != null && base.isPermissionSet(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return base.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return base.addAttachment(plugin, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return base.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return base.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public void recalculatePermissions() {
        if (getBase() == null) return;
        base.recalculatePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        if (getBase() == null) return;
        base.removeAttachment(attachment);
    }

    @Override
    public Server getServer() {
        return base.getServer();
    }

    @Override
    public void sendMessage(String message) {
        if (getBase() == null) return;
        base.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        if (getBase() == null) return;
        base.sendMessage(messages);
    }

    @Override
    public boolean isOp() {
        return getBase() != null && base.isOp();
    }

    @Override
    public void setOp(boolean value) {
        if (getBase() == null) return;
        base.setOp(value);
    }

}
