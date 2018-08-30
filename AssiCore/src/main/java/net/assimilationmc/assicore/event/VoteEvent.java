package net.assimilationmc.assicore.event;

import net.assimilationmc.assicore.player.AssiPlayer;

public class VoteEvent extends AssiEvent {

    private final AssiPlayer player;

    /**
     * Event called when a vote occurs.
     *
     * @param player the player who voted.
     */
    public VoteEvent(AssiPlayer player) {
        this.player = player;
    }

    /**
     * @return the player who voted.
     */
    public AssiPlayer getPlayer() {
        return player;
    }

}
