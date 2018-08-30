package net.assimilationmc.assicore.combat;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CombatLogger extends Module {

    private final List<String> denyCommands = Arrays.asList("spawn", "speed", "wspeed");
    private int secondsBeforeSafe;
    private boolean notifyInCombat;
    private Map<Player, Integer> inCombat;

    public CombatLogger(AssiPlugin plugin, int secondsBeforeSafe) {
        super(plugin, "Combat Logger");
        this.secondsBeforeSafe = secondsBeforeSafe;
        this.notifyInCombat = true;
    }

    @Override
    protected void start() {
        this.inCombat = Maps.newHashMap();
    }

    @Override
    protected void end() {
        this.inCombat.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(final EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) {
            if (!(e.getDamager() instanceof Arrow)) return;
            if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) return;
        }

        final Player attacked = (Player) e.getEntity();

        if (inCombat.containsKey(attacked)) {
            int task = inCombat.get(attacked);
            getPlugin().getServer().getScheduler().cancelTask(task);
        } else if (notifyInCombat) {
            attacked.sendMessage(C.II + ChatColor.BOLD + "You are now in combat, do not log out.");
        }

        inCombat.put(attacked, getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (attacked == null || !inCombat.containsKey(attacked)) return;
            inCombat.remove(attacked);
            if (notifyInCombat) attacked.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
        }, secondsBeforeSafe * 20));

    }

    @EventHandler
    public void on(final PlayerCommandPreprocessEvent e) {
        if (!inCombat.containsKey(e.getPlayer())) return;
        final String message = e.getMessage().replaceFirst("/", "");

        final String label = message.split(" ")[0];

        if (!denyCommands.contains(label.toLowerCase())) return;

        e.setCancelled(true);
        e.getPlayer().sendMessage(C.II + ChatColor.BOLD + "You cannot do that command in combat!");

    }

    @EventHandler
    public void on(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        cancelCount(player, true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (inCombat.containsKey(player)) {
            UtilServer.broadcast(C.SS + C.II + player.getName() + C.II + " combat logged! F");
            UtilServer.callEvent(new CombatLogEvent(player));
            cancelCount(player, false);
        }
    }

    public void cancelCount(Player player, boolean notify) {
        if (inCombat.containsKey(player)) {
            final Integer integer = inCombat.get(player);
            getPlugin().getServer().getScheduler().cancelTask(integer);
            inCombat.remove(player);
            if (notify && notifyInCombat) {
                player.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
            }
        }
    }

    public boolean isNotifyInCombat() {
        return notifyInCombat;
    }

    public void setNotifyInCombat(boolean notifyInCombat) {
        this.notifyInCombat = notifyInCombat;
    }

}
