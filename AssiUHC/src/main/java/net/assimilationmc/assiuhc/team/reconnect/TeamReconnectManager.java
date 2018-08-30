package net.assimilationmc.assiuhc.team.reconnect;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.team.TeamPlayerDisconnectSubscriber;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class TeamReconnectManager extends GameModule implements TeamPlayerDisconnectSubscriber {

    private Map<UUID, ReconnectData> reconnectDataMap;
    private Map<UUID, Integer> reconnectTimeout;

    public TeamReconnectManager(UHCGame assiUHC) {
        super(assiUHC, "Team Reconnect Manager", ModuleActivePolicy.WARMUP_GAME_END);
    }

    @Override
    public void start() {
        this.reconnectDataMap = Maps.newHashMap();
        this.reconnectTimeout = Maps.newHashMap();

        getAssiGame().getTeamManager().setDisconnectSubscriber(this);
    }

    @Override
    public void end() {
        reconnectDataMap.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        if (!reconnectDataMap.containsKey(player.getUniqueId())) return;

        if (!getAssiGame().getTeamManager().isAllowTeamReconnect()) {
            reconnectDataMap.remove(player.getUniqueId());
            return;
        }

        ReconnectData reconnectData = reconnectDataMap.get(player.getUniqueId());
        player.getInventory().clear();

        player.getInventory().setContents(reconnectData.getPlayerInventory().getContents());
        player.getInventory().setArmorContents(reconnectData.getPlayerInventory().getArmorContents());

        player.teleport(reconnectData.getLocation());

        reconnectDataMap.remove(player.getUniqueId());

        Bukkit.getScheduler().cancelTask(reconnectTimeout.get(player.getUniqueId()));
        reconnectTimeout.remove(player.getUniqueId());
    }

    @Override
    public void onDisconnect(Player player, GameTeam gameTeam) {
        if (getAssiGame().getGamePhase() == GamePhase.END) return;

        reconnectDataMap.put(player.getUniqueId(), new ReconnectData(player.getUniqueId(), player.getName(), player.getLocation(), player.getInventory()));

        UtilServer.broadcast(gameTeam.getColor() + gameTeam.getName() + " " + C.V + player.getName() + ChatColor.DARK_RED + " has disconnected! They have 5 minutes to return.");

        this.reconnectTimeout.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(getAssiGame().getPlugin(), () -> {
            ReconnectData reconnectData = reconnectDataMap.get(player.getUniqueId());
            if (reconnectData == null) return;

            UtilServer.broadcast(C.II + reconnectData.getName() + ChatColor.DARK_RED + " has been removed from the game for not connecting back.");

            reconnectDataMap.remove(reconnectData.getUuid());
            reconnectTimeout.remove(reconnectData.getUuid());

            ((UHCGame) getAssiGame()).getRewarded().add(reconnectData.getUuid());

        }, 5 * 20 * 60).getTaskId());

    }

    public Map<UUID, ReconnectData> getReconnectDataMap() {
        return reconnectDataMap;
    }

}
