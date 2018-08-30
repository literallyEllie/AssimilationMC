package net.assimilationmc.gameapi.spectate;

import net.assimilationmc.assicore.event.AssiEvent;
import net.assimilationmc.gameapi.team.GameTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerGameDeathEvent extends AssiEvent {

    private final Entity killer;
    private final GameTeam team;
    private Player player;
    private boolean perm;

    public PlayerGameDeathEvent(Player player, boolean perm, Entity killer, GameTeam team) {
        this.player = player;
        this.perm = perm;
        this.killer = killer;
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPerm() {
        return perm;
    }

    public void setPerm(boolean perm) {
        this.perm = perm;
    }

    public Entity getKiller() {
        return killer;
    }

    public GameTeam getTeam() {
        return team;
    }

}
