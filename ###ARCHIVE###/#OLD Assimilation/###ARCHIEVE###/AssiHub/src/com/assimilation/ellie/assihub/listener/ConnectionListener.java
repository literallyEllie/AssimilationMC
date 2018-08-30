package com.assimilation.ellie.assihub.listener;

import com.assimilation.ellie.assicore.util.Util;
import com.assimilation.ellie.assihub.AssiHub;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){

        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(AssiHub.getPlugin(AssiHub.class), () ->{
            for (int i = 0; i < 150; i++) {
                Util.mINFO_noP(p, "");
            }
        });

        p.getInventory().clear();
        p.getActivePotionEffects().clear();
        p.setHealthScale(20);
        p.setFoodLevel(20);
        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 30L, 20L);
        p.setWalkSpeed(0.6F);

        if(p.hasPermission("assihub.fly")){
            p.setAllowFlight(true);
            p.setFlying(true);
        }




    }

}
