package net.assimilationmc.ellie.assicore.api.plugin;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.command.AssiCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Created by Ellie on 9.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiChildPlugin {

    private String id;
    private String version;
    private String author;

    public AssiChildPlugin(String id, String version, String author) {
        this.id = id;
        this.version = version;
        this.author = author;
    }

    public String getID() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    public void onEnable() {
        logInfo("[" + version + "] " + id + " by " + author + " has been enabled.");
    }

    public void onDisable() {
        logInfo("[" + version + "] " + id + " by " + author + " has been disabled.");
    }

    public void registerCommand(AssiCommand command) {
        getAPI().getModuleManager().getCommandManager().registerCommand(command, id);
    }

    public void registerListener(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, AssiCore.getCore().getAssiPlugin());
    }

    public AssiCore getAPI() {
        return AssiCore.getCore();
    }

    public void logInfo(String message) {
        getAPI().getAssiPlugin().getLogger().info("[" + id + "] " + message);
    }

    public void logWarn(String message) {
        getAPI().getAssiPlugin().getLogger().warning("[" + id + "] " + message);
    }

    public void logError(String message) {
        getAPI().getAssiPlugin().getLogger().severe("[" + id + "] " + message);
    }

}
