package net.assimilationmc.assibungee.discord;

public enum DiscordPresetChannel {

    BOT_LOGS(302421090683060224L),
    STAFF_CHAT(304929844716634112L),
    ADMIN(301733172050132992L),
    BROADCAST(418498591687180288L),
    HELPOP(304931386865745920L),
    PUNISH_LOG(419449509827837952L),

    PUBLIC_GENERAL(301711310926774283L),
    GAME_FEED(472823263874121737L),

    ;

    private final long id;

    /**
     * Preset Discord channel IDs that correspond to pre-existing discord channels
     * These are noted as official channels and should never be deleted without the API being updated too.
     *
     * @param id The channel ID
     */
    DiscordPresetChannel(long id) {
        this.id = id;
    }

    /**
     * Gets the channel ID
     *
     * @return the channel ID
     */
    public long getId() {
        return id;
    }

}
