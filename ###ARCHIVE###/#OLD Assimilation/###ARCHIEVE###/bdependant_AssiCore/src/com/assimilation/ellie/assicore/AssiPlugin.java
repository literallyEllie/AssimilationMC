package com.assimilation.ellie.assicore;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.listener.ChatListener;
import com.assimilation.ellie.assicore.listener.ConnectionListener;
import com.assimilation.ellie.assicore.listener.WorldListener;
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
        }catch(Throwable e){
            e.printStackTrace();
        }

        Bukkit.getPluginManager().registerEvents(new ChatListener(assiCore.getModuleManager().getConfigManager().getChatFormats()), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);

        Bukkit.getPluginManager().registerEvents(new WorldListener(assiCore.getModuleManager().getConfigManager().isDisableWeather(),
                assiCore.getModuleManager().getConfigManager().isDisablePvP(), assiCore.getModuleManager().getConfigManager().isDisableDangerousBlocks()), this);



        logI("AssiCore has been enabled");
    }

    @Override
    public void onDisable() {

        this.assiCore.finish("abasdsdfdsfgasdjk34jkasdjkdf");
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
