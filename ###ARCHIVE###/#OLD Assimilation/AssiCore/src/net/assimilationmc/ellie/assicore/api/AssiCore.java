package net.assimilationmc.ellie.assicore.api;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.task.EnjinUpdateTask;
import net.assimilationmc.ellie.assicore.task.MonitorTask;
import net.assimilationmc.ellie.assicore.task.fakeenjin.rpc.RPCData;
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

    private MonitorTask monitorTask;

    private ModuleManager moduleManager;

    private HashSet<UUID> vanishedPlayers;

    private final String serverID;

    private EnjinUpdateTask enjinUpdateTask;

    public AssiCore(AssiPlugin assiPlugin) {
        setAssiCore(this);
        this.assiPlugin = assiPlugin;

        this.moduleManager = new ModuleManager(this);
        moduleManager.load();

        this.serverID = moduleManager.getConfigManager().getServerID();
        this.vanishedPlayers = new HashSet<>();

        this.serverState = moduleManager.getConfigManager().getServerState();

        this.monitorTask = new MonitorTask();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(assiPlugin, monitorTask, 1000, 50);

        enjinUpdateTask = new EnjinUpdateTask(moduleManager.getConfigManager().getEnjinAuth());
        RPCData<Boolean> data = enjinUpdateTask.auth(Bukkit.getPort(), true);
        if (data == null) {
            logW("Auth key invalid!");
        } else if (data.getError() != null) {
            logW("Auth key invalid: " + data.getError().getMessage());
        } else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(assiPlugin, enjinUpdateTask, 1200L, 1200L);
        }


    }

    public void finish() {

        this.vanishedPlayers.clear();
        this.moduleManager.unload();
        Bukkit.getScheduler().cancelTasks(assiPlugin);
        assiCore = null;
        this.monitorTask = null;
        this.vanishedPlayers.clear();
        this.serverState = ServerState.STABLE;
        this.assiPlugin = null;

    }

    public String getServerID() {
        return serverID;
    }

    public void logI(String info) {
        Bukkit.getLogger().info("[AssiCore] " + info);
    }

    public void logW(String warn) {
        Bukkit.getLogger().warning("[AssiCore] " + warn);
    }

    public void logE(String error) {
        Bukkit.getLogger().severe("[AssiCore] " + error);
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
        moduleManager.getConfigManager().setServerState(serverState);
    }

    public HashSet<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public MonitorTask getMonitorTask() {
        return monitorTask;
    }

}
