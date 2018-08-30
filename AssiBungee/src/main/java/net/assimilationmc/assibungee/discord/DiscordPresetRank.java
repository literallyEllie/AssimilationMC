package net.assimilationmc.assibungee.discord;

import net.assimilationmc.assibungee.rank.Rank;

public enum DiscordPresetRank {

    PLAYER(470896400679567361L),
    DONATOR(301713402915586050L),
    CONTENT_CREATOR(345905188135108619L),
    BUILDER(301712984072257537L),
    DEVELOPER(301712518315900929L),
    HELPER(301712907400642561L),
    MODERATOR(301712779541479424L),
    ADMIN(301711628980846592L),
    STAFF_MANAGER(301712670229528578L),;

    private long id;

    DiscordPresetRank(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static DiscordPresetRank fromRank(Rank rank) {
        switch (rank) {
            case PLAYER:
            case BETA:
                return PLAYER;
            case DEMONIC:
            case INFERNAL:
                return DONATOR;
            case YOUTUBE:
            case STREAMER:
                return CONTENT_CREATOR;
            case MANAGER:
                return STAFF_MANAGER;

        }

        for (DiscordPresetRank discordPresetRank : values()) {
            if (discordPresetRank.name().equalsIgnoreCase(rank.getName()))
                return discordPresetRank;
        }

        return null;
    }

}
