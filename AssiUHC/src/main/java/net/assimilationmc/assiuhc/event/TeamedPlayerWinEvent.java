package net.assimilationmc.assiuhc.event;

import net.assimilationmc.assicore.event.AssiEvent;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.entity.Player;

public class TeamedPlayerWinEvent extends AssiEvent {

    private Player player;
    private AssiPlayer assiPlayer;

    public TeamedPlayerWinEvent(Player player, AssiPlayer assiPlayer) {
        this.player = player;
        this.assiPlayer = assiPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public AssiPlayer getAssiPlayer() {
        return assiPlayer;
    }

}
