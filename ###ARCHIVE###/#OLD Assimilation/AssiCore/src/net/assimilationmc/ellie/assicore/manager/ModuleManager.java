package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.api.economy.CoinEconomy;
import net.assimilationmc.ellie.assicore.api.economy.Economy;
import net.assimilationmc.ellie.assicore.api.economy.EconomyManager;
import net.assimilationmc.ellie.assicore.api.plugin.AssiPluginManager;
import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ModuleManager {

    private AssiCore assiCore;
    private static ModuleManager moduleManager;
    private Map<String, IManager> moduleManagers;
    private ScoreboardManager scoreboardManager;

    public ModuleManager(AssiCore assiCore){
        moduleManager = this;
        this.moduleManagers = new HashMap<>();
        this.assiCore = assiCore;
    }

    public void load() throws RuntimeException {

        ConfigManager configManager = new ConfigManager(assiCore);
        if(!configManager.load())
            throw new RuntimeException("Failed to load ConfigManager! Not loading anything else.");
        else moduleManagers.put(configManager.getModuleID(), configManager);

        SQLManager sqlManager = new SQLManager(assiCore.getAssiPlugin(), configManager.getHost(), configManager.getPort(), configManager.getDatabase(), configManager.getUsername(), configManager.getPassword());
        if(!sqlManager.load()) throw new RuntimeException("Failed to load SQLManager! Shutting down.");
        else moduleManagers.put(sqlManager.getModuleID(), sqlManager);

        SecurityManager securityManager = new SecurityManager();
        if(!securityManager.load()){
            assiCore.logE("Failed to load SecurityManager!");
        }else moduleManagers.put(securityManager.getModuleID(), securityManager);

        CommandManager commandManager = new CommandManager();
        if(!commandManager.load()){
            assiCore.logE("Failed to load CommandManager!");
        }else moduleManagers.put(commandManager.getModuleID(), commandManager);

        PermissionManager permissionManager = new PermissionManager();
        if(!permissionManager.load()){
            assiCore.logE("Failed to load PermissionManager!");
        }else moduleManagers.put(permissionManager. getModuleID(), permissionManager);

        PlayerManager playerManager = new PlayerManager();
        if(!playerManager.load()){
            assiCore.logE("Failed to load PlayerManager!");
        }else moduleManagers.put(playerManager.getModuleID(), playerManager);

        scoreboardManager = new ScoreboardManager();
        Bukkit.getPluginManager().registerEvents(scoreboardManager, assiCore.getAssiPlugin());
       // Bukkit.getScheduler().scheduleSyncRepeatingTask(assiCore.getAssiPlugin(), () -> scoreboardManager.build(), 20L, 150L);

        StaffChatManager staffChatManager = new StaffChatManager();
        if(!staffChatManager.load()){
            assiCore.logE("Failed to load StaffChatManager!");
        }else moduleManagers.put(staffChatManager.getModuleID(), staffChatManager);

        FriendManager friendManager = new FriendManager(assiCore);
        if(!friendManager.load()){
            assiCore.logE("Failed to load SQLManager!");
        }else moduleManagers.put(friendManager.getModuleID(), friendManager);

        BroadcastManager broadcastManager = new BroadcastManager();
        if(!broadcastManager.load()){
            assiCore.logE("Failed to load FriendManager!");
        }else moduleManagers.put(broadcastManager.getModuleID(), broadcastManager);

        try {
            Class<?> clazz = Class.forName(configManager.getEconomy());
            if(Economy.class.isAssignableFrom(CoinEconomy.class)){
                Economy economy = (Economy) clazz.newInstance();
                if(economy instanceof CoinEconomy) ((CoinEconomy) economy).setPlayerManager(playerManager);
                EconomyManager economyManager = new EconomyManager(economy);
                if(!economyManager.load())
                    assiCore.logE("Failed to load Economy manager!");
                else moduleManagers.put(economyManager.getModuleID(), economyManager);
            }else{
                assiCore.logE("Class "+configManager.getEconomy()+" does not implement Economy.class!");
            }
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchElementException e){
            e.printStackTrace();
            assiCore.logE("Failed to load Economy!");
        }

        AssiPluginManager assiPluginManager = new AssiPluginManager();
        if(!assiPluginManager.load()){
            assiCore.logE("Failed to load PluginManager!");
        }else {
            moduleManagers.put(assiPluginManager.getModuleID(), assiPluginManager);
            assiPluginManager.loadExternalPlugins(assiCore.getAssiPlugin().getDataFolder());
        }


    }

    public void unload(){
        moduleManagers.values().forEach(IManager::unload);
        moduleManagers.clear();
        assiCore = null;
    }

    public ConfigManager getConfigManager(){
        return (ConfigManager) moduleManagers.get("config");
    }

    public SQLManager getSQLManager(){
        return (SQLManager) moduleManagers.get("sql");
    }

    public SecurityManager getSecurityManager(){ return (SecurityManager) moduleManagers.get("security"); }

    public FriendManager getFriendManager(){ return (FriendManager) moduleManagers.get("friend"); }

    public CommandManager getCommandManager(){
        return (CommandManager) moduleManagers.get("commands");
    }

    public PermissionManager getPermissionManager(){
        return (PermissionManager) moduleManagers.get("permission");
    }

    public PlayerManager getPlayerManager(){
        return (PlayerManager) moduleManagers.get("players");
    }

    public StaffChatManager getStaffChatManager(){
        return (StaffChatManager) moduleManagers.get("staffchat");
    }

    public BroadcastManager getBroadcastManager(){
        return (BroadcastManager) moduleManagers.get("broadcast");
    }

    public EconomyManager getEconomyManager() { return (EconomyManager) moduleManagers.get("economy"); }

    public Map<String, IManager> getModuleManagers() {
        return Collections.unmodifiableMap(moduleManagers);
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public AssiPluginManager getAssiPluginManager(){
        return (AssiPluginManager) moduleManagers.get("plugin");
    }

}
