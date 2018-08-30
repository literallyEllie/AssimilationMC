package com.assimilation.ellie.assikits;

import com.assimilation.ellie.assikits.kit.KitManager;
import com.assimilation.ellie.assikits.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ellie on 18/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiKits extends JavaPlugin {

    private KitManager kitManager;
    private UserManager userManager;

    @Override
    public void onEnable() {

        this.kitManager = new KitManager(this);
        this.userManager = new UserManager(this);



        logI("AssiKits has loaded up");
    }

    @Override
    public void onDisable() {


        this.kitManager = null;
        this.userManager = null;

        logI("AssiKits has been disabled");
    }

    public void logI(String info){
        Bukkit.getLogger().info("[AssiKits] "+info);
    }

    public void logW(String warn){
        Bukkit.getLogger().warning("[AssiKits] "+warn);
    }

    public void logE(String error){
        Bukkit.getLogger().severe("[AssiKits] "+error);
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

}
