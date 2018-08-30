package net.assimilationmc.assiuhc.game.teamed.scatter;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assiuhc.border.GameBorder;
import net.assimilationmc.assiuhc.chat.CmdChatMode;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.spectate.PlayerGameDeathEvent;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;

public class GameTeamedScatter extends UHCTeamedGame {

    private Map<Player, ItemPlayerTracker> playerTrackers;

    public GameTeamedScatter(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("SCATTER_TEAMED", "Scatter",
                "Communications are out and equipped with only a tracker and your voice you must reassemble your team to become victorious.",
                UHCGameSubType.TEAMED_SCATTER.name()));

        getAssiGameSettings().setWarmUpTime(10 * 60);
        getAssiGameSettings().setMaxGameTime(45 * 60);

        getAssiGameSettings().setMinPlayers(8);
        getAssiGameSettings().setMaxPlayers(48);

        getUHCTeamManager().setMaxTeamSize(4);

        plugin.getChatManager().setChatPolicy(new ScatterChatPolicy(this));

        getPlugin().getCommandManager().registerCommand(new CmdShout(this));
        getPlugin().getCommandManager().unregisterCommand(CmdChatMode.class);

        playerTrackers = Maps.newHashMap();

        setGameBorder(new GameBorder(this));
    }

    @Override
    public GameTeam electWinner() {
        final List<GameTeam> remainingTeams = getUHCTeamManager().getRemainingTeams();
        return remainingTeams.size() == 1 ? remainingTeams.get(0) : null;
    }

    @Override
    public int winnerXp() {
        return 10;
    }

    @Override
    public int winnerBucks() {
        return 20;
    }

    @Override
    public int winnerUC() {
        return 2;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void instructions(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        if (getGamePhase() == GamePhase.LOBBY) {
            player.sendMessage(C.C);
            player.sendMessage(GC.C + "Welcome to " + GC.II + "Scatter" + GC.C + ". Here is a short guide of how to play:");
            player.sendMessage(C.C);
            player.sendMessage(C.C + "You will be spawned in a " + GC.C + "random location" + C.C + " on a large map.");
            player.sendMessage(C.C + "To have the highest chance of winning, you " + GC.C + "must find" + C.C + " all your team mates.");
            player.sendMessage(GC.C + ChatColor.BOLD + "HOWEVER " + C.C + "When spawning, your team communications were cut and you can only shout.");
            player.sendMessage(C.C + "When shouting (" + GC.C + "/shout" + C.C + "), everyone in a " + GC.C + "45 block radius" + C.C + " to you will hear your call");
            player.sendMessage(C.C + "The rest you have to rely on your " + GC.C + "Tracker");
            player.sendMessage(C.C + "If you talk normally, only players in a " + GC.C + "15 block radius" + C.C + " will hear you.");
            player.sendMessage(C.C);

        }

        if (getGamePhase() == GamePhase.WARMUP || getGamePhase() == GamePhase.IN_GAME) {
            if (!getTeamReconnectManager().getReconnectDataMap().containsKey(player.getUniqueId())) return;
            GameTeam team = getTeamManager().getTeam(player);
            if (team == null) return;
            playerTrackers.put(player, new ItemPlayerTracker(player, team));

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void trackerManagement(final GamePhaseChangeEvent e) {
        final GamePhase to = e.getTo();
        if (to == GamePhase.WARMUP) {

            for (Player player : getLivePlayers()) {
                GameTeam team = getTeamManager().getTeam(player);
                if (team == null) continue;
                playerTrackers.put(player, new ItemPlayerTracker(player, team));
            }
        }

    }

    @EventHandler
    public void trackerManagement(final PlayerGameDeathEvent e) {
        if (getGamePhase() == GamePhase.WARMUP || getGamePhase() == GamePhase.IN_GAME) {
            ItemPlayerTracker itemPlayerTracker = playerTrackers.get(e.getPlayer());
            if (itemPlayerTracker == null) return;
            itemPlayerTracker.removeItem();

            playerTrackers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        if (getGamePhase() == GamePhase.WARMUP || getGamePhase() == GamePhase.IN_GAME) {
            ItemPlayerTracker itemPlayerTracker = playerTrackers.get(e.getPlayer());
            if (itemPlayerTracker == null) return;
            itemPlayerTracker.removeItem();

            playerTrackers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void trackerUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.TWO_SEC) return;
        playerTrackers.values().forEach(itemPlayerTracker -> itemPlayerTracker.tick(this));
    }

    @EventHandler
    public void onIterate(final PlayerInteractEvent e)  {
        if (getGamePhase() == GamePhase.WARMUP || getGamePhase() == GamePhase.IN_GAME) {
            final Player player = e.getPlayer();
            if (e.getItem() != null && e.getItem().getType() == Material.COMPASS && e.getAction().name().contains("RIGHT_CLICK")) {

                final ItemPlayerTracker itemPlayerTracker = playerTrackers.get(player);
                if (itemPlayerTracker != null) {
                    itemPlayerTracker.onRightClick(this);
                }
            }
        }
    }

    @EventHandler
    public void noPartyChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        Party party = getPlugin().getPartyManager().getPartyOf(player, false);
        if (party == null) return;

        if (party.getChatToggled().contains(player)) {
            party.getChatToggled().remove(player);
            player.sendMessage(ChatColor.AQUA + ChatColor.ITALIC.toString() + "Now is not the time for partying...");
        }

    }

}
