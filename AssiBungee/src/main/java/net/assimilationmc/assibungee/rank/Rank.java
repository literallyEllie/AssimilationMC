package net.assimilationmc.assibungee.rank;

import net.md_5.bungee.api.ChatColor;

public enum Rank {

    PLAYER("User", ChatColor.GRAY),
    BETA("Tester", ChatColor.YELLOW),

    DEMONIC("Demonic", ChatColor.DARK_RED),
    INFERNAL("Infernal", ChatColor.GOLD),

    BUILDER("Builder", ChatColor.DARK_AQUA),

    YOUTUBE("YouTuber", ChatColor.RED),
    STREAMER("Streamer", ChatColor.DARK_PURPLE),

    DEVELOPER("Developer", ChatColor.BLUE),

    HELPER("Helper", ChatColor.GREEN),
    MOD("Moderator", ChatColor.AQUA),
    ADMIN("Admin", ChatColor.RED),
    MANAGER("Manager", ChatColor.DARK_PURPLE),
    OWNER("Owner", ChatColor.LIGHT_PURPLE),;

    private String name;
    private ChatColor chatColor;

    /**
     * Constant ranks.
     *
     * @param name      The name of the rank.
     * @param chatColor the chat color of the player.
     */
    Rank(String name, ChatColor chatColor) {
        this.name = name;
        this.chatColor = chatColor;
    }

    /**
     * If a rank by "name" exists
     *
     * @param name Name to query
     * @return If the rank is PLAYER ?
     */
    public static boolean exists(String name) {
        return fromString(name) != null;
    }

    /**
     * Match a rank with a display
     *
     * @param name Name of said rank
     * @return The rank matching that name or {@link Rank#PLAYER}
     */
    public static Rank fromString(String name) {
        for (Rank rank : values()) {
            if (rank.name().equalsIgnoreCase(name) || rank.getName().equalsIgnoreCase(name))
                return rank;
        }
        return PLAYER;
    }

    /**
     * Get rank name
     *
     * @return Uncolored rank name
     */
    public String getName() {
        return name;
    }

    /**
     * Get rank chat color
     *
     * @return Chat color of the rank
     */
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * @return the prefix to the rank.
     */
    public String getPrefix() {
        return isDefault() ? chatColor.toString()
                : chatColor + ChatColor.BOLD.toString() + this.name + ChatColor.RESET;
    }

    /**
     * Evaluates if said rank is higher than the selected rank
     *
     * @param rank Rank to compare
     * @return If {@param rank} is higher than this.
     */
    public boolean isHigherThanOrEqualTo(Rank rank) {
        return this.ordinal() >= rank.ordinal();
    }

    /**
     * Evaluates if the current rank is default
     *
     * @return If the current rank is {@link Rank#PLAYER}
     */
    public boolean isDefault() {
        return this == PLAYER;
    }

    /**
     * Evaluates if the current rank is a promoter
     *
     * @return If the current rank is {@link Rank#YOUTUBE} or {@link Rank#STREAMER}
     */
    public boolean isPromoter() {
        return this == YOUTUBE || this == STREAMER;
    }

    /**
     * Evaluates if the current rank is a Donator
     *
     * @return If the current rank is {@link Rank#DEMONIC} or {@link Rank#INFERNAL}
     */
    public boolean isDonator() {
        return this == DEMONIC || this == INFERNAL;
    }

    /**
     * Rank ordinal ID.
     *
     * @return The ordinal value of the current rank
     */
    public int getId() {
        return this.ordinal();
    }

}
