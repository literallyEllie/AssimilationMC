package com.assimilation.ellie.assicore.api;

import com.assimilation.ellie.assicore.AssiPlugin;
import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.task.MonitorTask;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Ellie on 26/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public final class AssiCore {

    private AssiPlugin assiPlugin;
    private static AssiCore assiCore;
    private ServerState serverState;

    private MonitorTask moniterTask;

    private ModuleManager moduleManager;

    private HashSet<UUID> vanishedPlayers;

    private final String serverID;

    public AssiCore(AssiPlugin assiPlugin){
        setAssiCore(this);
        this.assiPlugin = assiPlugin;

        this.moduleManager = new ModuleManager(this);
        moduleManager.load();

        this.serverID = moduleManager.getConfigManager().getServerID();
        this.vanishedPlayers = new HashSet<>();

        this.serverState = ServerState.STABLE;

        this.moniterTask = new MonitorTask();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(assiPlugin, moniterTask, 1000, 50);


    }

    public void finish(String pass){
        if(pass.equals("abasdsdfdsfgasdjk34jkasdjkdf")){

            this.vanishedPlayers.clear();
            this.moduleManager.unload();
            Bukkit.getScheduler().cancelTasks(assiPlugin);
            assiCore = null;
            this.moniterTask = null;
            this.vanishedPlayers.clear();
            this.serverState = ServerState.STABLE;
            this.assiPlugin = null;

        }

    }

    public String getServerID() {
        return serverID;
    }

    public void logI(String info){
        Bukkit.getLogger().info("[AssiCore] "+info);
    }

    public void logW(String warn){ Bukkit.getLogger().info("[AssiCore] "+warn); }

    public void logE(String error){
        Bukkit.getLogger().info("[AssiCore] "+error);
    }

    public AssiPlugin getAssiPlugin() {
        return assiPlugin;
    }

    private void setAssiCore(AssiCore assiCore) {
        AssiCore.assiCore = assiCore;
    }

    public static AssiCore getCore() {
        return assiCore;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    public HashSet<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public MonitorTask getMonitorTask() {
        return moniterTask;
    }
}
