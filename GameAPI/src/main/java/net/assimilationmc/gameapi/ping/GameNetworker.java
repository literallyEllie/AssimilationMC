package net.assimilationmc.gameapi.ping;

import net.assimilationmc.assicore.event.GamePingEvent;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class GameNetworker extends GameModule {

    public GameNetworker(AssiGame game) {
        super(game, "Game Networker", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }

    @EventHandler
    public void on(final ServerListPingEvent e) {
        e.setMotd(GC.C + "AssimilationMC Game Server (" + getAssiGame().getAssiGameMeta().getDisplay() + ") - " + getAssiGame().getGamePhase());
    }

    @EventHandler
    public void on(final AsyncPlayerPreLoginEvent e) {
        switch (getAssiGame().getGamePhase()) {
            case END:
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, GC.II + "This game has already ended!");
                break;
        }
    }

    @EventHandler
    public void on(final GamePingEvent e) {
        e.addAttribute("game_phase", getAssiGame().getGamePhase().toString());

        if (getAssiGame().getGameMapManager().getSelectedWorld() != null) {
            e.addAttribute("map", getAssiGame().getGameMapManager().getSelectedWorld().getName());
        }
        if (getAssiGame().getGamePhase() != GamePhase.LOBBY) {
            e.addAttribute("start", getAssiGame().getStart());
        }

        e.addAttribute("game_max_players", getAssiGame().getAssiGameSettings().getMaxPlayers());
        e.addAttribute("game_type", getAssiGame().getAssiGameMeta().getSubType());
    }

}
