package net.assimilationmc.gameapi.testgame;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.scoreboard.ScoreboardPolicy;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.List;

public class TestGame extends AssiGame {

    public TestGame(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("TEST", "A test game", "A game to test the api", System.getProperty("subType")));


        GameTeam team = new GameTeam("Test", ChatColor.GREEN, true);
        getTeamManager().addTeam(team);
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() != UpdateType.SEC) return;

        if (getGamePhase() == GamePhase.IN_GAME) {
            if (getCounter() == 30) {
                setGamePhase(GamePhase.END);
                UtilServer.broadcast("Game end.");
            }
        }

    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        if (e.getTo() == GamePhase.WARMUP || e.getTo() == GamePhase.IN_GAME) {

            getPlugin().getScoreboardManager().setScoreboardPolicy(new ScoreboardPolicy(getPlugin()) {

                @Override
                public List<String> getSideBar(AssiPlayer player) {
                    List<String> testBoard = Lists.newArrayList();

                    testBoard.add("lol!!");

                    return testBoard;
                }

            });

        }

    }

    @Override
    public void onGameEnd() {

    }
}
