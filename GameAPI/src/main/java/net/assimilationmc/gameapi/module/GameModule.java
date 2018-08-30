package net.assimilationmc.gameapi.module;

import net.assimilationmc.gameapi.game.AssiGame;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public abstract class GameModule implements Listener {

    private final AssiGame assiGame;
    private final String display;
    private final ModuleActivePolicy moduleActivePolicy;

    private boolean active;

    public GameModule(AssiGame assiGame, String display, ModuleActivePolicy moduleActivePolicy) {
        this.assiGame = assiGame;
        this.display = display;
        this.moduleActivePolicy = moduleActivePolicy;
        this.active = false;

        assiGame.getGameModuleHandle().registerModule(this);
    }

    public abstract void start();

    public abstract void end();

    protected AssiGame getAssiGame() {
        return assiGame;
    }

    public String getDisplay() {
        return display;
    }

    public ModuleActivePolicy getModuleActivePolicy() {
        return moduleActivePolicy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected void log(Level level, String message) {
        assiGame.getPlugin().getLogger().log(level, "[" + getDisplay() + "] " + message);
    }

    protected void log(String message) {
        this.log(Level.INFO, message);
    }

}
