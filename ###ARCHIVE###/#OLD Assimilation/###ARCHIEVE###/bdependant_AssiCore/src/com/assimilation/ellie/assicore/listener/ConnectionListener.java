package com.assimilation.ellie.assicore.listener;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.util.PermissionLib;
import com.assimilation.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConnectionListener implements Listener {

    private ModuleManager moduleManager = AssiCore.getCore().getModuleManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){

        Player player = e.getPlayer();

        try {
            ModuleManager.getModuleManager().getPermissionManager().playerJoin(player);
        }catch(NullPointerException ex){}

        if(player.isOp() && !moduleManager.getSecurityManager().checkPlayer(player.getUniqueId())){
            player.setOp(false);
            player.sendMessage(Util.prefix()+Util.color("&cYou have been automatically deoped for security precautions."));
        }



        if(!AssiCore.getCore().getVanishedPlayers().isEmpty()  && !player.hasPermission(PermissionLib.BYPASS.VANISH)){

            AssiCore.getCore().getVanishedPlayers().forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
                if(p != null){
                    player.hidePlayer(p);
                }
            });
        }

        if(moduleManager.getConfigManager().isForceSpawn() && moduleManager.getConfigManager().getSpawn() != null){
            player.teleport(moduleManager.getConfigManager().getSpawn().toLocation());
        }

        e.setJoinMessage(moduleManager.getConfigManager().getJoinMessage(e.getPlayer().getName()));

    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        ModuleManager.getModuleManager().getPermissionManager().playerLeave(player);

        if(player.isOp() && !moduleManager.getSecurityManager().checkPlayer(player.getUniqueId())){
            player.setOp(false);
        }

        if(AssiCore.getCore().getVanishedPlayers().contains(player.getUniqueId())){
            AssiCore.getCore().getVanishedPlayers().remove(player.getUniqueId());
        }

        e.setQuitMessage(moduleManager.getConfigManager().getLeaveMessage(e.getPlayer().getName()));
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent e){
        World from = e.getFrom();
        World to = e.getPlayer().getWorld();

        if(from != null && from != to){
            ModuleManager.getModuleManager().getPermissionManager().playerWorldSwitch(e.getPlayer());

        }

    }

}
