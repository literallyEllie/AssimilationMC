package net.assimilationmc.gameapi.util;

import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class UtilAutoLapis implements Listener  {

    private final ItemStack lapis;

    public UtilAutoLapis() {
        this.lapis = new ItemBuilder(Material.INK_SACK).setColor(ItemBuilder.StackColor.BLUE)
                .setAmount(23).build();
    }

    @EventHandler
    public void on(final InventoryOpenEvent e) {
        if (!(e.getInventory() instanceof EnchantingInventory)) return;
        e.getInventory().setItem(1, lapis);
    }

    @EventHandler
    public void on(final InventoryCloseEvent e) {
        if (!(e.getInventory() instanceof EnchantingInventory)) return;
        e.getInventory().setItem(1, null);
    }

    @EventHandler
    public void on(final InventoryClickEvent e) {
        if (!(e.getInventory() instanceof EnchantingInventory) || e.getSlot() != 1) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void on(final EnchantItemEvent e) {
        e.getInventory().setItem(1, lapis);
    }

}
