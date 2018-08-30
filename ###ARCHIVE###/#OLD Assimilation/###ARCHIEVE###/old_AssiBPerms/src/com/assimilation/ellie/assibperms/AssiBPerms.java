package com.assimilation.ellie.assibperms;

import com.assimilation.ellie.assibperms.backend.GroupManager;
import com.assimilation.ellie.assibperms.backend.UserManager;
import com.assimilation.ellie.assibperms.command.CmdPermission;
import com.assimilation.ellie.assibperms.command.PCommandManager;
import com.assimilation.ellie.assibperms.listener.ConnectionListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by Ellie on 22/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiBPerms extends Plugin {

    private static AssiBPerms assiBPerms;

    private GroupManager groupManager;
    private UserManager userManager;
    private PCommandManager commandManager;

    @Override
    public void onEnable() {
        setAssiBungee(this);

        if(ProxyServer.getInstance().getPluginManager().getPlugin("AssiBungee") == null){
            logE("Failed to find bungee core plugin! Plugin will not operate.");
            return;
        }

        this.groupManager = new GroupManager();
        this.userManager = new UserManager(groupManager);

        this.commandManager = new PCommandManager();


        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdPermission(commandManager));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ConnectionListener());
        logI("AssiBungee has loaded successfully.");
    }

    @Override
    public void onDisable() {


        if(this.groupManager != null) {
            this.groupManager.finish();
            this.groupManager = null;
        }

        setAssiBungee(null);
        logI("AssiBungee has been disabled.");
    }

    private void setAssiBungee(AssiBPerms assiBPerms) {
        AssiBPerms.assiBPerms = assiBPerms;
    }

    public static AssiBPerms getAssiBPerms() {
        return assiBPerms;
    }

    public void logI(String info){
        getProxy().getLogger().info("[AssiBPerms] "+info);
    }

    public void logW(String warn){
        getProxy().getLogger().warning("[AssiBPerms] "+warn);
    }

    public void logE(String error){
        getProxy().getLogger().severe("[AssiBPerms] "+error);
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public PCommandManager getCommandManager() {
        return commandManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
