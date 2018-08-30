package net.assimilationmc.gameapi.module;

import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class GameModuleHandle implements Listener {

    private final AssiGame game;

    public GameModuleHandle(AssiGame game) {
        this.game = game;
        game.getPlugin().registerListener(this);
    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        final GamePhase to = e.getTo();

        game.getGameModules().forEach(gameModule -> checkModule(gameModule, to));
    }

    public void registerModule(GameModule gameModule) {
        game.getGameModules().add(gameModule);

        checkModule(gameModule, game.getGamePhase());
    }

    public void activateModule(GameModule module) {
        if (module.isActive()) return;
        try {
            module.start();
            module.setActive(true);
            game.getPlugin().registerListener(module);
        } catch (Throwable e) {
            game.getPlugin().getLogger().severe("Error whilst enabling module " + module.getDisplay() + "!");
            e.printStackTrace();
            module.setActive(false);
        }
    }

    public void deactivateModule(GameModule module) {
        if (!module.isActive()) return;
        try {
            module.end();
        } catch (Throwable e) {
            game.getPlugin().getLogger().severe("Error whilst disabling module " + module.getDisplay() + "!");
            e.printStackTrace();
        } finally {
            HandlerList.unregisterAll(module);
            module.setActive(false);
        }
    }

    public void checkModule(GameModule gameModule, GamePhase to) {
        switch (gameModule.getModuleActivePolicy()) {
            case LOBBY:
                if (!gameModule.isActive() && to == GamePhase.LOBBY) {
                    activateModule(gameModule);
                    break;
                } else if (gameModule.isActive() && to != GamePhase.LOBBY) {
                    deactivateModule(gameModule);
                }
                break;
            case GAME:
                if (!gameModule.isActive() && to == GamePhase.IN_GAME) {
                    activateModule(gameModule);
                } else if (gameModule.isActive() && to != GamePhase.IN_GAME) {
                    deactivateModule(gameModule);
                }
                break;
            case GAME_AND_END:
                if (!gameModule.isActive() && (to == GamePhase.IN_GAME || to == GamePhase.END)) {
                    activateModule(gameModule);
                } else if (gameModule.isActive() && to == GamePhase.LOBBY || to == GamePhase.WARMUP) {
                    deactivateModule(gameModule);
                }
                break;
            case WARMUP_GAME_END:
                if (!gameModule.isActive() && (to == GamePhase.WARMUP || to == GamePhase.IN_GAME || to == GamePhase.END)) {
                    activateModule(gameModule);
                } else if (gameModule.isActive() && to == GamePhase.LOBBY) {
                    deactivateModule(gameModule);
                }
                break;
            case END:
                if (!gameModule.isActive() && to == GamePhase.END) {
                    activateModule(gameModule);
                } else if (gameModule.isActive() && to != GamePhase.END) {
                    deactivateModule(gameModule);
                }
                break;
            case PERMANENT:
                if (!gameModule.isActive()) {
                    activateModule(gameModule);
                }
                break;
        }


    }

}
