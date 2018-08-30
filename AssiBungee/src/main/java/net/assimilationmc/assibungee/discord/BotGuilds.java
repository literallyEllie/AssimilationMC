package net.assimilationmc.assibungee.discord;

public enum BotGuilds {

    PUBLIC(301711310926774283L),
    STAFF(301729195644551168L);

    private final long id;

    /**
     * Preset Discord Guild IDs that correspond to all the guilds the bot should ever exist in.
     *
     * @param id the Guild ID
     */
    BotGuilds(long id) {
        this.id = id;
    }

    /**
     * @return the Guild ID.
     */
    public long getId() {
        return id;
    }

}
