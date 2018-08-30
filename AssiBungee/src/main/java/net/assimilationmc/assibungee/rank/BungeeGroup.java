package net.assimilationmc.assibungee.rank;

public enum BungeeGroup {

    PLAYER,
    STAFF,
    ADMIN,
    SUPERADMIN;

    /**
     * A helpful method to safely decode an input string into a {@link BungeeGroup}
     *
     * @param input the input string.
     * @return The group, or default if there was no match.
     */
    public static BungeeGroup fromString(String input) {
        for (BungeeGroup bungeeGroup : values()) {
            if (bungeeGroup.name().equalsIgnoreCase(input))
                return bungeeGroup;
        }
        return PLAYER;
    }

    /**
     * Is a group higher than or equal to this.
     *
     * @param group the group to compare.
     * @return if the group specified is higher than or equal to this.
     */
    public boolean isHigherThanOrEqualTo(BungeeGroup group) {
        return this.ordinal() >= group.ordinal();
    }


}
