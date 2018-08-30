package net.assimilationmc.ellie.assicore.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class WorldListener implements Listener {

    private boolean weather, pvp, dangerousBlocks;

    public WorldListener(boolean weather, boolean pvp, boolean dangerousBlocks){
        this.weather = weather;
        this.pvp = pvp;
        this.dangerousBlocks = dangerousBlocks;
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e){
        if(weather) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        if(pvp) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent e){
        if(dangerousBlocks) {
            switch (e.getBlock().getType()) {
                case TNT:
                    e.setCancelled(true);
                    e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    break;
                case LAVA:
                    e.setCancelled(true);
                    e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    break;
                case FLINT_AND_STEEL:
                    e.setCancelled(true);
                    e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    break;
                case LAVA_BUCKET:
                    e.setCancelled(true);
                    e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    break;
                case FIRE:
                    e.setCancelled(true);
                    e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent e){
        if(dangerousBlocks) {
            e.setCancelled(true);
            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            e.setItemStack(new ItemStack(Material.AIR));
        }
    }

}
