package net.assimilationmc.assibungee.discord;

import net.assimilationmc.assibungee.rank.Rank;

import java.util.UUID;

public class DiscordLinkData {

    private final long discordId;
    private final UUID uuid;
    private final String name;
    private final Rank rank;

    public DiscordLinkData(long discordId, UUID uuid, String name, Rank rank) {
        this.discordId = discordId;
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
    }

    public long getDiscordId() {
        return discordId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Rank getRank() {
        return rank;
    }

}
