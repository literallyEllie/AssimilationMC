package net.assimilationmc.assiuhc.event;

import net.assimilationmc.assicore.event.AssiEvent;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import org.bukkit.entity.Player;

public class SinglesWinEvent extends AssiEvent {

    private Player player;
    private UHCPlayer uhcPlayer;

    public SinglesWinEvent(Player player, UHCPlayer uhcPlayer) {
        this.player = player;
        this.uhcPlayer = uhcPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public UHCPlayer getUhcPlayer() {
        return uhcPlayer;
    }
}
