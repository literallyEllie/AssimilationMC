package com.assimilation.ellie.assibungee;

import com.assimilation.ellie.assibungee.command.*;
import com.assimilation.ellie.assibungee.command.helpop.CmdHelpOP;
import com.assimilation.ellie.assibungee.listener.ConnectionListener;
import com.assimilation.ellie.assibungee.listener.InputListener;
import com.assimilation.ellie.assibungee.manager.ModuleManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Handler;

/**
 * Created by Ellie on 19/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiBungee extends Plugin {

    private static AssiBungee assiBungee;

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        setAssiBungee(this);

        this.moduleManager = new ModuleManager(this);
        try {
            moduleManager.load();
        }catch(RuntimeException e){
            logE("Failed to start up! "+e.getMessage());
            e.printStackTrace();
            this.shutdown();
            return;
        }
        Command[] commands = {new CmdAlert(), new CmdHelpOP(), new CmdMessage(), new CmdReply(), new CmdAssi(), new CmdMaintenance()};

        for (int i = 0; i < commands.length; i++) {
            ProxyServer.getInstance().getPluginManager().registerCommand(this, commands[i]);
        }

        Listener[] listeners = {new ConnectionListener(), new InputListener()};

        for (int i = 0; i < listeners.length; i++) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, listeners[i]);
        }

        logI("AssiBungee has loaded successfully.");

    }

    @Override
    public void onDisable() {

        if(moduleManager != null){
            moduleManager.unload();
            moduleManager = null;
        }

        logI("AssiBungee has been disabled.");
        setAssiBungee(null);
    }

    public void logI(String info){
        getProxy().getLogger().info("[AssiBungee] "+info);
    }

    public void logW(String warn){
        getProxy().getLogger().info("[AssiBungee] "+warn);
    }

    public void logE(String error){
        getProxy().getLogger().severe("[AssiBungee] "+error);
    }

    private void setAssiBungee(AssiBungee assiBungee) {
        AssiBungee.assiBungee = assiBungee;
    }

    public static AssiBungee getAssiBungee() {
        return assiBungee;
    }

    private void shutdown(){
        this.onDisable();

        for(Handler handler: this.getLogger().getHandlers()){
            handler.close();
        }

        getProxy().getPluginManager().unregisterCommands(this);
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getScheduler().cancel(this);
        this.getExecutorService().shutdownNow();

        Thread.getAllStackTraces().keySet().forEach(thread -> {
            if (thread.getClass().getClassLoader() == this.getClass().getClassLoader()) {
                try {
                    thread.interrupt();
                    thread.join(2000);
                    if (thread.isAlive()) {
                        thread.stop();
                    }
                } catch (Throwable t) {
                    logE("Failed to shutdown plugin.");
                }
            }
        });
    }

}
