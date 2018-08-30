package net.assimilationmc.assibungee.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.rank.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssiPlayer implements CommandSender {

    private final UUID uuid;
    private int id;
    private ProxiedPlayer base;
    private String name;
    private List<String> previousNames;

    private String displayName;
    private long firstSeen, lastSeen;
    private long discordAccount;

    private Rank rank;
    private String[] ips;

    private int bucks, ultraCoins;
    private int joins, votes, voteStreak;
    private long lastVote;
    private String referredBy;
    private int helpopHandled;

    private String lastSeenServer;
    private FriendData friendData;
    private Map<String, Integer> boosters;
    private Map<String, Long> achievements;
    private Map<String, String> achievementProgress;
    private Map<String, Long> lastRewardClaim;

    /**
     * Custom Player wrapper sort of thing.
     * Contains unique information about a player
     *
     * @param player Player to base it around
     */
    public AssiPlayer(ProxiedPlayer player) {
        this(player.getUniqueId());
        this.base = player;
        this.name = player.getName();
        this.displayName = player.getDisplayName();
        this.lastSeen = System.currentTimeMillis();
    }

    public AssiPlayer(UUID uuid) {
        this.uuid = uuid;
        this.previousNames = Lists.newLinkedList();
        this.friendData = new FriendData(uuid);
        this.boosters = Maps.newHashMap();
        this.achievements = Maps.newHashMap();
        this.lastRewardClaim = Maps.newHashMap();
    }

    /**
     * Player database index basically
     *
     * @return Player ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set player index
     *
     * @param id Index
     */
    public void setId(int id) {
        this.id = id;
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
    public ProxiedPlayer getBase() {
        return base;
    }

    /**
     * Set the player base
     *
     * @param base Underlying player object.
     */
    public void setBase(ProxiedPlayer base) {
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
        if (isOnline()) base.setDisplayName(displayName);

    }

    /**
     * Resets their name to the default value it should be.
     * Was made for the caching, but if that is removed, this can go too.
     */
    public void resetName() {
        setDisplayName((rank.isDefault() ? ChatColor.GRAY : rank.getPrefix() + " " + ChatColor.RESET) + name);
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
        this.bucks = bucks;
    }

    /**
     * Add some bucks.
     *
     * @param bucks The amount of bucks to add.
     */
    public void addBucks(int bucks) {
        setBucks(this.bucks + bucks);
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
        this.ultraCoins = ultraCoins;
    }

    /**
     * Add some ultra coins.
     *
     * @param ultraCoins The amount of ultra coins to add.
     */
    public void addUltraCoins(int ultraCoins) {
        setUltraCoins(this.ultraCoins + ultraCoins);
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
     * @param joins Joins on the account
     */
    public void setJoins(int joins) {
        this.joins = joins;
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
    }

    /**
     * @return the HelpOPs handled by the player.
     */
    public int getHelpopsHandled() {
        return helpopHandled;
    }

    /**
     * Set the amount of HelpOPs they've handled.
     *
     * @param helpopsHandled the amount of HelpOPs handled.
     */
    public void setHelpopsHandled(int helpopsHandled) {
        this.helpopHandled = helpopsHandled;
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
    }

    public Map<String, Long> getAchievements() {
        return achievements;
    }

    public void setAchievements(Map<String, Long> achievements) {
        this.achievements = achievements;
    }

    public Map<String, String> getAchievementProgress() {
        return achievementProgress;
    }

    public void setAchievementProgress(Map<String, String> achievementProgress) {
        this.achievementProgress = achievementProgress;
    }

    public Map<String, Long> getLastRewardClaim() {
        return lastRewardClaim;
    }

    public void setLastRewardClaim(Map<String, Long> lastRewardClaim) {
        this.lastRewardClaim = lastRewardClaim;
    }


    /**
     * Returns if player is online
     *
     * @return the underlying player base isn't null and the base is online.
     */
    public boolean isOnline() {
        return base != null;
    }

    /*
     * BELOW IS IMPLEMENTED METHODS OF CommandSender
     */

    @Override
    public Collection<String> getPermissions() {
        return base.getPermissions();
    }

    @Override
    public boolean hasPermission(String s) {
        return base.hasPermission(s);
    }

    @Override
    public void setPermission(String s, boolean b) {
        base.setPermission(s, b);
    }

    @Override
    public Collection<String> getGroups() {
        return base.getGroups();
    }

    @Override
    public void addGroups(String... strings) {
        base.addGroups(strings);
    }

    @Override
    public void removeGroups(String... strings) {
        base.removeGroups(strings);
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        base.sendMessage(baseComponent);
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        base.sendMessage(baseComponents);
    }

    @Override
    public void sendMessage(String s) {
        base.sendMessage(new TextComponent(s));
    }

    @Override
    public void sendMessages(String... strings) {
        base.sendMessages(strings);
    }

}
