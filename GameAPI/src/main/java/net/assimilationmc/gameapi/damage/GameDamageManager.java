package net.assimilationmc.gameapi.damage;

import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GameDamageManager extends GameModule {

    public GameDamageManager(AssiGame game) {
        super(game, "Game Damage Manager", ModuleActivePolicy.WARMUP_GAME_END);
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player damager = (Player) e.getDamager();


        if (e.getEntity() instanceof Player) {
            if (!getAssiGame().getAssiGameSettings().isPvp()) {
                e.setCancelled(true);
                return;
            }
            if (getAssiGame().getAssiGameSettings().isFriendlyFire()) return;
            Player entity = (Player) e.getEntity();

            GameTeam damageTeam = getAssiGame().getTeamManager().getTeam(damager);
            if (damageTeam == null) return;
            GameTeam entityTeam = getAssiGame().getTeamManager().getTeam(entity);
            if (entityTeam == null) return;

            if (damageTeam.getName().equals(entityTeam.getName())) {
                e.setCancelled(true);
            }

        } else if (!getAssiGame().getAssiGameSettings().isPve()) e.setCancelled(true);

    }


}
