package net.assimilationmc.ellie.assiuhc;

import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.backend.MapManager;
import net.assimilationmc.ellie.assiuhc.backend.SQLManager;
import net.assimilationmc.ellie.assiuhc.backend.SettingManager;
import net.assimilationmc.ellie.assiuhc.backend.SignManager;
import net.assimilationmc.ellie.assiuhc.command.CommandManager;
import net.assimilationmc.ellie.assiuhc.games.GameManager;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHC extends JavaPlugin {

    public static final String prefix = Util.color("&c&lUHC "+ UColorChart.R);

    private SettingManager settingManager;
    private SQLManager sqlManager;
    private MapManager mapManager;
    private GameManager gameManager;
    private CommandManager commandManager;
    private SignManager signManager;

    @Override
    public void onEnable() {

        this.settingManager = new SettingManager(this);
        this.sqlManager = new SQLManager(this);
        this.mapManager = new MapManager(this);
        this.gameManager = new GameManager();
        this.commandManager = new CommandManager();
        this.signManager = new SignManager(this);

        logI("UHC has been enabled");
    }

    @Override
    public void onDisable() {

        gameManager.finish();
        gameManager = null;

        signManager.finish();
        signManager = null;

        mapManager.finish();
        mapManager = null;

        commandManager.finish();
        commandManager = null;

        logI("UHC has been disabled");
    }

    public void logI(String info){
        Bukkit.getLogger().info("[AssiUHC] "+info);
    }

    public void logW(String warn){
        Bukkit.getLogger().warning("[AssiUHC] "+warn);
    }

    public void logE(String error){
        Bukkit.getLogger().severe("[AssiUHC] "+error);
    }

    public SettingManager getSettingManager() {
        return settingManager;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

}
