package net.assimilationmc.assicore.event;

import net.assimilationmc.assicore.player.AssiPlayer;

public class PlayerJoinNetworkEvent extends AssiEvent {

    private final AssiPlayer player;

    public PlayerJoinNetworkEvent(AssiPlayer player) {
        this.player = player;
    }

    public AssiPlayer getPlayer() {
        return player;
    }

}
