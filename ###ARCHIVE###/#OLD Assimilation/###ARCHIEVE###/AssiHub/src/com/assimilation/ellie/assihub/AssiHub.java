package com.assimilation.ellie.assihub;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.util.Util;
import com.assimilation.ellie.assihub.listener.ConnectionListener;
import com.assimilation.ellie.assihub.listener.PlayerListener;
import com.assimilation.ellie.assihub.score.ScoreboardData;
import com.assimilation.ellie.assihub.score.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiHub extends JavaPlugin {

    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        scoreboardManager = new ScoreboardManager();
        Bukkit.getPluginManager().registerEvents(scoreboardManager, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> scoreboardManager.build(), 20L, 150L);
        scoreInit();

        logI("AssiHub has been loaded");
    }

    @Override
    public void onDisable() {


        logI("AssiHub has been disabled");
    }

    public void logI(String info){
        Bukkit.getLogger().info("[AssiHub] "+info);
    }

    public void logW(String warn){
        Bukkit.getLogger().warning("[AssiHub] "+warn);
    }

    public void logE(String error){
        Bukkit.getLogger().severe("[AssiHub] "+error);
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    private void scoreInit(){
        ScoreboardData data = scoreboardManager.getData("default", true);

        data.writeEmpty();

        data.write(Util.color("&aServer"));
        try {
            data.write(Util.color("&7")+AssiCore.getCore().getServerID());
        }catch(NullPointerException e){
            data.write(Util.color("&aUnknown"));
        }
        data.writeEmpty();

        data.write(Util.color("&aRank"));
        data.writeRank();
        data.writeEmpty();
    }

}
