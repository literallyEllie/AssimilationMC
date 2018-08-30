package com.assimilation.ellie.assihub.listener;

import com.assimilation.ellie.assicore.manager.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent e){
        ((Player) e.getEntity()).setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (ModuleManager.getModuleManager().getConfigManager().getSpawn() != null) {
            e.setRespawnLocation(ModuleManager.getModuleManager().getConfigManager().getSpawn().toLocation());
        }
    }

    @EventHandler
    public void onDmg(EntityDamageEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(e.getPlayer().isFlying()){
            Bukkit.getOnlinePlayers().forEach(o -> o.playEffect(e.getPlayer().getLocation(), Effect.POTION_SWIRL, (byte) 0));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(!e.getPlayer().hasPermission("assihub.break")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(!e.getPlayer().hasPermission("assihub.drop")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        if(!e.getPlayer().hasPermission("assihub.pickup")){
            e.setCancelled(true);
        }
    }





}
