package net.assimilationmc.assibungee;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.plugin.Listener;

import java.util.logging.Level;

public abstract class Module implements Listener {

    private final AssiBungee plugin;
    private final String display;
    private final long startTime;

    /**
     * Server Base Module
     *
     * @param plugin  Base Plugin
     * @param display Logger display name
     */
    public Module(AssiBungee plugin, String display) {
        this.plugin = plugin;
        this.display = display;
        this.startTime = System.currentTimeMillis();
        plugin.registerListener(this);
        plugin.getModules().add(this);
        start();
    }

    protected abstract void start();

    protected abstract void end();

    /***
     * Get base plugin
     * @return Base plugin instance
     */
    public final AssiBungee getPlugin() {
        return plugin;
    }

    /**
     * Get the Module display name
     *
     * @return Module display name
     */
    public final String getDisplay() {
        return display;
    }

    /**
     * Get an unused value of start time, could be used for timings.
     *
     * @return When the module started up.
     */
    public final long getStartTime() {
        return startTime;
    }

    /**
     * Log to console
     *
     * @param message Message to log
     */
    protected final void log(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log prefixed message to console.
     *
     * @param level   Message level
     * @param message Message to log
     */
    protected final void log(Level level, String message) {
        plugin.getLogger().log(level, "[" + display + "] " + message);
    }

    /**
     * A rough dependency requirement checker
     *
     * @param name   Reference name for the module
     * @param module Potentially null module instance.
     * @throws NullPointerException By {@link Preconditions#checkNotNull(Object)} if the module isn't loaded.
     */
    protected void requireAndThrow(String name, Module module) throws NullPointerException {
        Preconditions.checkNotNull(module, "Module " + display + " requires " + name + " to run!");
    }

}

