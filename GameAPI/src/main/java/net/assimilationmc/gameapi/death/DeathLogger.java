package net.assimilationmc.gameapi.death;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.UtilTime;
import net.assimilationmc.gameapi.spectate.PlayerGameDeathEvent;
import net.assimilationmc.gameapi.team.GameTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;
import java.util.UUID;

public class DeathLogger implements Listener {

    private final Map<UUID, Integer> kills;
    private final Map<Player, Entity> lastDamage;
    private final Map<UUID, Long> deaths;
    private final Map<UUID, GameTeam> deceasedTeams;

    public DeathLogger() {
        this.kills = Maps.newHashMap();
        this.lastDamage = Maps.newHashMap();
        this.deaths = Maps.newHashMap();
        this.deceasedTeams = Maps.newHashMap();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(final EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        final Player damaged = (Player) e.getEntity();
        final Entity damager = e.getDamager();

        if (lastDamage.containsKey(damaged)) {
            lastDamage.replace(damaged, damager);
        } else lastDamage.put(damaged, damager);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerGameDeathEvent e) {
        if (!(e.getKiller() instanceof Player)) return;
        Player killer = (Player) e.getKiller();
        int killsOf = getKillsOf(killer);

        final Player killed = e.getPlayer();

        if (killsOf != 0) {
            kills.replace(killed.getUniqueId(), killsOf + 1);
        } else kills.put(killer.getUniqueId(), 1);

        deaths.put(killed.getUniqueId(), UtilTime.now());

        if (e.getTeam() != null) {
            deceasedTeams.put(killed.getUniqueId(), e.getTeam());
        }

    }

    public Map<UUID, Integer> getKills() {
        return kills;
    }

    public Integer getKillsOf(Player player) {
        return kills.getOrDefault(player.getUniqueId(), 0);
    }

    public Map<Player, Entity> getLastDamage() {
        return lastDamage;
    }

    public Entity getLastDamagedBy(Player player) {
        return lastDamage.get(player);
    }

    public boolean hasDied(Player player) {
        return deaths.containsKey(player.getUniqueId());
    }

    public long getDied(UUID uuid) {
        return deaths.getOrDefault(uuid, (long) -1);
    }

    public GameTeam getOldTeam(Player player) {
        return deceasedTeams.get(player.getUniqueId());
    }

}
