package net.assimilationmc.gameapi.spectate;

import net.assimilationmc.assicore.event.AssiEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerGamePreRespawnEvent extends AssiEvent implements Cancellable {

    private final Player player;
    private boolean cancel;

    public PlayerGamePreRespawnEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
