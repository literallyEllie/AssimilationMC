package net.assimilationmc.assicore.world;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldPreserver implements Listener {

    private final World world;
    private boolean protectPlayers, stopPlayerInteract;

    public WorldPreserver(World world) {
        this.world = world;
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doFireTick", "false");
    }

    public World getWorld() {
        return world;
    }

    public boolean isProtectPlayers() {
        return protectPlayers;
    }

    public void setProtectPlayers(boolean protectPlayers) {
        this.protectPlayers = protectPlayers;
    }

    public boolean isStopPlayerInteract() {
        return stopPlayerInteract;
    }

    public void setStopPlayerInteract(boolean stopPlayerInteract) {
        this.stopPlayerInteract = stopPlayerInteract;
    }

    @EventHandler
    public void on(final ExplosionPrimeEvent e) {
        if (e.getEntity().getWorld().equals(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final WeatherChangeEvent e) {
        if (e.getWorld().equals(world) && e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final BlockFromToEvent e) {
        if (e.getBlock().getWorld().equals(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final BlockSpreadEvent e) {
        if (e.getBlock().getWorld().equals(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final BlockFadeEvent e) {
        if (e.getBlock().getWorld().equals(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final LeavesDecayEvent e) {
        if (e.getBlock().getWorld().equals(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final EntityDamageEvent e) {
        if (protectPlayers && e.getEntity().getWorld().equals(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final FoodLevelChangeEvent e) {
        if (protectPlayers && e.getEntity().getWorld().equals(world)) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void on(final BlockBreakEvent e) {
        final Player player = e.getPlayer();
        if (!stopPlayerInteract || !player.getWorld().equals(world)) return;
        if (player.getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(final InventoryClickEvent e) {
        if (!stopPlayerInteract || !e.getWhoClicked().getWorld().equals(world)) return;

        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null && e.getClickedInventory().getName().equals("container.inventory") &&
                e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final PlayerDropItemEvent e) {
        if (!stopPlayerInteract || !e.getPlayer().getWorld().equals(world)) return;
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerPickupItemEvent e) {
        if (!stopPlayerInteract || !e.getPlayer().getWorld().equals(world)) return;
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerItemDamageEvent e) {
        if (!stopPlayerInteract || !e.getPlayer().getWorld().equals(world)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        if (!stopPlayerInteract) return;
        final Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) return;
        if (!clickedBlock.getWorld().equals(world)) return;

        if ((clickedBlock.getType() == Material.SOIL || clickedBlock.getRelative(BlockFace.UP).getType() == Material.CROPS && e.getAction() == Action.PHYSICAL)
                || clickedBlock.getType().name().contains("DOOR")) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setCancelled(true);
        }
    }


}
