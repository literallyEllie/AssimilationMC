package com.assimilation.ellie.assicore.manager;

import com.assimilation.ellie.assicore.api.AssiCore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ModuleManager {

    private AssiCore assiCore;
    private static ModuleManager moduleManager;
    private Map<String, IManager> moduleManagers;

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

        FriendManager friendManager = new FriendManager(assiCore);
        if(!sqlManager.load()){
            assiCore.logE("Failed to load FriendManager!");
        }else moduleManagers.put(friendManager.getModuleID(), friendManager);
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

    public Map<String, IManager> getModuleManagers() {
        return Collections.unmodifiableMap(moduleManagers);
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }


}
