package net.assimilationmc.uhclobbyadaptor.stats;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.lobby.LobbyScorePolicy;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilMessage;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.stats.comp.CompRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class StatsDisplayer implements Listener {

    private UHCLobbyAdaptor plugin;
    private Map<UUID, Integer> tasks;

    public StatsDisplayer(UHCLobbyAdaptor plugin) {
        this.plugin = plugin;
        tasks = Maps.newHashMap();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getAssiPlugin().getScoreboardManager().setScoreboardPolicy(new LobbyScorePolicy(plugin.getAssiPlugin()) {
            @Override
            public String getPlayerTagSuffix(AssiPlayer viewerPlayer, AssiPlayer player) {
                UHCPlayer uhcPlayer = plugin.getROuhcStatsProvider().getPlayer(player.getBase());
                return (uhcPlayer.getCompRank() == CompRank.UNRANKED ? null : uhcPlayer.getCompRank().getSuffix());
            }
        });
    }

    public void setupPlayer(Player player) {
        restartTaskHotbar(player);
    }

    public void restartTaskHotbar(Player player) {
        stopPlayerTasks(player);

        tasks.put(player.getUniqueId(), new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.getWorld().getName().equals("Lobby")) return;
                UHCPlayer uhcPlayer = plugin.getROuhcStatsProvider().getPlayer(player);
                UtilMessage.sendHotbar(player, C.II + "Your UHC Level: " + C.V + uhcPlayer.getLevel() + C.C + " + " + C.V + uhcPlayer.getXp() + C.II + "xp");
            }

        }.runTaskTimer(plugin, 10, 40).getTaskId());

    }

    public void stopPlayerTasks(Player player) {
        if (tasks.containsKey(player.getUniqueId())) {
            int task = tasks.get(player.getUniqueId());
            Bukkit.getScheduler().cancelTask(task);

            tasks.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        setupPlayer(e.getPlayer());
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        stopPlayerTasks(e.getPlayer());
    }

    public Map<UUID, Integer> getTasks() {
        return tasks;
    }
}


