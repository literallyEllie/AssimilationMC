package com.assimilation.ellie.assibungee.listener;

import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.server.ServerState;
import com.assimilation.ellie.assibungee.util.AssiServerInfo;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConnectionListener implements Listener {

    private ModuleManager moduleManager = ModuleManager.getModuleManager();

    @EventHandler
    public void onConnect(ServerConnectEvent  e){

        AssiServerInfo from = null;

        if(e.getPlayer().getServer() != null) from = moduleManager.getServerManager().getServer(e.getPlayer().getServer().getInfo().getName());

        AssiServerInfo to = moduleManager.getServerManager().getServer(e.getTarget().getName());

        if(e.getPlayer().getServer() == null){

            if(moduleManager.getServerManager().getNextLobby() == null){

                if(moduleManager.getServerManager().canJoin(e.getPlayer(), to.getID())){
                    Util.mINFO(e.getPlayer(), "Connecting you to &9"+to.getID()+"&f (No other available lobbies).");
                    Util.mWARN(e.getPlayer(), "This server is in &9" + to.getServerState().toString() + "&c mode.");

                }else {
                    e.setCancelled(true);
                    e.getPlayer().disconnect(new TextComponent(Util.color("&cThe network is currently full or there are no servers available! Sorry :o .. Try again soon.")));
                }
                return;

            }
        }else {

            if (!moduleManager.getServerManager().canJoin(e.getPlayer(), from.getID())) {
                e.setCancelled(true);
                Util.mWARN(e.getPlayer(), "This server is currently unavailable, check back later");
                return;


            }else {

                Util.mINFO(e.getPlayer(), "Connecting you to &9" + to.getID() + "&f.");

                if (!to.getServerState().equals(ServerState.STABLE)) {
                    Util.mWARN(e.getPlayer(), "This server is in &9" + to.getServerState().toString() + "&c mode.");
                }
                return;
            }
        }

        ServerInfo s = moduleManager.getServerManager().getNextLobby();
        Util.mINFO(e.getPlayer(), "Connecting you to &9"+s.getName()+"&f.");
        e.setTarget(s);

    }

    @EventHandler
    public void onJoin(PostLoginEvent e){
        ProxiedPlayer proxiedPlayer = e.getPlayer();

        moduleManager.getPlayerManager().loadOnlinePlayer(proxiedPlayer, true);


    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent e){
        ProxiedPlayer proxiedPlayer = e.getPlayer();

        moduleManager.getPlayerManager().unloadPlayer(proxiedPlayer.getName());
    }

    @EventHandler
    public void onLogin(LoginEvent e){

        if(BungeeCord.getInstance().getPlayer(e.getConnection().getUniqueId()) != null){
            e.setCancelReason(Util.prefix()+"\n\n"+Util.color("&cYou are already connected to this server"));
            e.setCancelled(true);

        }



    }


}
