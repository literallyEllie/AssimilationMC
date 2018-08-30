package net.assimilationmc.ellie.assipunish;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assipunish.command.CmdPunish;
import net.assimilationmc.ellie.assipunish.punish.data.PunishManager;
import net.assimilationmc.ellie.assipunish.task.PunishmentValidateTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiPunish extends JavaPlugin {

    private static AssiPunish assiPunish;
    private PunishManager punishManager;

    private PunishmentValidateTask punishmentValidateTask;

    public String ipFilterPermission = "assipunish.punish.allowIp";

    @Override
    public void onEnable() {
        assiPunish = this;

        punishManager = new PunishManager();
        punishManager.load();

        ModuleManager.getModuleManager().getCommandManager().registerCommand(new CmdPunish());


        punishmentValidateTask = new PunishmentValidateTask(punishManager);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, punishmentValidateTask, 20L, 6000L);
        Bukkit.getPluginManager().registerEvents(punishmentValidateTask, this);

        logI("AssiPunish has been enabled");
    }

    @Override
    public void onDisable() {

        if(punishmentValidateTask != null){
            Bukkit.getScheduler().cancelTasks(this);
            punishmentValidateTask = null;
        }

        if(punishManager != null){
            punishManager.unload();
            punishManager = null;
        }


        logI("AssiPunish has been disabled");
        assiPunish = null;
    }

    public static AssiPunish getAssiPunish() {
        return assiPunish;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }

    public PunishmentValidateTask getPunishmentValidateTask() {
        return punishmentValidateTask;
    }

    public void logI(String info){
        Bukkit.getLogger().info("[AssiPunish] "+info);
    }

    public void logW(String warn){
        Bukkit.getLogger().warning("[AssiPunish] "+warn);
    }

    public void logE(String error){
        Bukkit.getLogger().severe("[AssiPunish] "+error);
    }



}
