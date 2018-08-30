package net.assimilationmc.ellie.assicore;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.listener.ChatListener;
import net.assimilationmc.ellie.assicore.listener.ConnectionListener;
import net.assimilationmc.ellie.assicore.listener.LobbyListener;
import net.assimilationmc.ellie.assicore.listener.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ellie on 26/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public final class AssiPlugin extends JavaPlugin  {

    private AssiCore assiCore;

    @Override
    public void onEnable() {

        try {
            this.assiCore = new AssiCore(this);
        }catch(Throwable e) {
            e.printStackTrace();
            logE("Error setting up the core!");
        }

        try {
            Bukkit.getPluginManager().registerEvents(new ChatListener(assiCore.getModuleManager().getConfigManager().getChatFormats()), this);
            Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
            Bukkit.getPluginManager().registerEvents(new LobbyListener(), this);

            Bukkit.getPluginManager().registerEvents(new WorldListener(assiCore.getModuleManager().getConfigManager().isDisableWeather(),
                    assiCore.getModuleManager().getConfigManager().isDisablePvP(), assiCore.getModuleManager().getConfigManager().isDisableDangerousBlocks()), this);
        }catch(Throwable e){
            e.printStackTrace();
            logE("Error whilst setting up listeners!");
        }


        logI("AssiCore has been enabled");
    }

    @Override
    public void onDisable() {

        this.assiCore.finish();
        this.assiCore = null;

        logW("AssiCore has been disabled");
    }


    public void logI(String info){
        Bukkit.getLogger().info("[AssiCore] "+info);
    }

    public void logW(String warn){
        Bukkit.getLogger().warning("[AssiCore] "+warn);
    }

    public void logE(String error){
        Bukkit.getLogger().severe("[AssiCore] "+error);
    }



}
