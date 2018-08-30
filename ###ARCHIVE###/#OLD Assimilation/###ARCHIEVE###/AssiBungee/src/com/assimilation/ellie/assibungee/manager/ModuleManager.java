package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.AssiBungee;

import java.util.HashMap;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ModuleManager {

    private AssiBungee assiBungee;
    private static ModuleManager moduleManager;
    private HashMap<String, IManager> moduleManagers;

    public ModuleManager(AssiBungee assiBungee){
        moduleManager = this;
        this.moduleManagers = new HashMap<>();
        this.assiBungee = assiBungee;
    }

    public void load() throws RuntimeException {

        ServerManager serverManager = new ServerManager();
        if(!serverManager.load()){
            assiBungee.logE("Failed to load module "+serverManager.getModuleID()+"!");
        }else moduleManagers.put(serverManager.getModuleID(), serverManager);

        ConfigManager configManager = new ConfigManager(assiBungee);
        if(!configManager.load()){
            throw new RuntimeException("Failed to load ConfigManager! Not loading anything else.");
        }else moduleManagers.put(configManager.getModuleID(), configManager);

        SQLManager sqlManager = new SQLManager(assiBungee, configManager.getHost(), configManager.getPort(), configManager.getDatabase(), configManager.getUsername(), configManager.getPassword());
        if(!sqlManager.load()){
            throw new RuntimeException("Failed to load SQLManager! Shutting down.");
        }else moduleManagers.put(sqlManager.getModuleID(), sqlManager);

        PlayerManager playerManager = new PlayerManager();
        if(!playerManager.load()){
            throw new RuntimeException("Failed to load SQLManager! Not loading anything else.");
        }else moduleManagers.put(playerManager.getModuleID(), playerManager);

        HelpOPManager helpOPManager = new HelpOPManager();
        if(!helpOPManager.load()){
            assiBungee.logE("Failed to load module "+helpOPManager.getModuleID()+"!");
        }else moduleManagers.put(helpOPManager.getModuleID(), helpOPManager);

        StaffChatManager staffChatManager = new StaffChatManager();
        if(!staffChatManager.load()){
            assiBungee.logE("Failed to load module "+staffChatManager.getModuleID()+"!");
        }else moduleManagers.put(staffChatManager.getModuleID(), staffChatManager);

        BroadcastManager broadcastManager = new BroadcastManager();
        if(!broadcastManager.load()){
            assiBungee.logE("Failed to load module "+broadcastManager.getModuleID()+"!");
        }else moduleManagers.put(broadcastManager.getModuleID(), broadcastManager);


    }

    public void unload(){
        moduleManagers.values().forEach(IManager::unload);
        moduleManagers.clear();
        assiBungee = null;
    }

    public ConfigManager getConfigManager(){
        return (ConfigManager) moduleManagers.get("config");
    }

    public SQLManager getSQLManager(){
        return (SQLManager) moduleManagers.get("sql");
    }

    public PlayerManager getPlayerManager(){
        return (PlayerManager) moduleManagers.get("players");
    }

    public ServerManager getServerManager(){
        return (ServerManager) moduleManagers.get("server");
    }

    public HelpOPManager getHelpOPManager(){
        return (HelpOPManager) moduleManagers.get("helpop");
    }

    public StaffChatManager getStaffChatManager(){
        return (StaffChatManager) moduleManagers.get("staffchat");
    }

    public BroadcastManager getBroadcastManager(){
        return (BroadcastManager) moduleManagers.get("broadcast");
    }

    public HashMap<String, IManager> getModuleManagers() {
        return moduleManagers;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
}
