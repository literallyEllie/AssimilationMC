package net.assimilationmc.ellie.assicore.listener;

import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Ellie on 20/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class LobbyListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        Block b = e.getClickedBlock();
        if (b != null && b.getType().equals(Material.SKULL)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                BlockState bs = b.getState();
                Skull skull = (Skull) bs;
                if (skull.getSkullType().equals(SkullType.PLAYER) && skull.hasOwner()) {

                    p.sendMessage(Util.color("&7---------------------------------"));
                    p.sendMessage(Util.color("&aOwner: &c"+skull.getOwner()));
                    p.sendMessage(Util.color("&aOnline: &c"+ Bukkit.getOfflinePlayer(skull.getOwner()).isOnline()));
                    p.sendMessage(Util.color("&7---------------------------------"));
                }
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        Player player = e.getPlayer();
        if(player.getWorld().getName().equals("Lobby") && !player.hasPermission(PermissionLib.BYPASS.HUB)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if(player.getWorld().getName().equals("Lobby") && !player.hasPermission(PermissionLib.BYPASS.HUB)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHealthLoss(EntityDamageEvent e){
        if(e.getEntity() instanceof Player && e.getEntity().getWorld().getName().equals("Lobby")) e.setCancelled(true);
    }

    @EventHandler
    public void onInventory(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player && e.getClickedInventory().getName().equals("container.inventory") &&
                e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked().getWorld().getName().equals("Lobby")){
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (player.getWorld().getName().equals("Lobby") && player.isFlying()) {
            if (player.hasPermission(PermissionLib.STAFF_CHAT)) {
                Bukkit.getOnlinePlayers().forEach(p -> p.playEffect(player.getLocation(), Effect.COLOURED_DUST, 20));
            } else if (player.hasPermission(PermissionLib.LOBBY.FLY)) {
                Bukkit.getOnlinePlayers().forEach(p -> p.playEffect(player.getLocation(), Effect.CLOUD, 20));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        if(player.getWorld().getName().equals("Lobby") && !player.hasPermission(PermissionLib.LOBBY.ADMIN)){
            e.setCancelled(true);
        }
    }



}
