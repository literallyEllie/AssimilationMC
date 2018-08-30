package net.assimilationmc.assicore.combat;

import net.assimilationmc.assicore.event.AssiEvent;
import org.bukkit.entity.Player;

public class CombatLogEvent extends AssiEvent {

    private Player player;

    /**
     * The event called when a player combat logs.
     *
     * @param player the player who combat logged.
     */
    public CombatLogEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
