package net.assimilationmc.ellie.assicore.api.ui;

import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ellie on 20/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class DynamicUI {

    private Inventory inventory;
    private Player player;

    private HashMap<Integer, IButton> buttons = new HashMap<>();

    private List<Integer> borderSlots;
    private ItemStack boarderStack;

    public DynamicUI(int size, String display, Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, Util.color(display));
    }

    public DynamicUI(int size, String display, Player player, List<Integer> borderSlots, ItemStack boarderStack) {
        this(size, display, player);
        this.borderSlots = borderSlots;
        this.boarderStack = boarderStack;
    }

    public Inventory build() {
        if (boarderStack != null) {
            borderSlots.forEach(slot -> inventory.setItem(slot, boarderStack));
        }
        return inventory;
    }

    public void addButton(int slot, IButton button, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
        buttons.put(slot, button);
    }

    public void removeButton(int slot) {
        inventory.setItem(slot, null);
        buttons.remove(slot);
    }

    public void closeInventory() {
        player.closeInventory();
    }

    public boolean isButton(int slot) {
        return buttons.containsKey(slot);
    }

    public void handleClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getUniqueId() == player.getUniqueId()) {
            e.setCancelled(true);
            ItemStack itemStack = e.getCurrentItem();

            int slot = e.getRawSlot();
            if (itemStack != null && itemStack.getType() != Material.AIR && buttons.containsKey(slot)) {
                buttons.get(slot).onClick(player, e.getClick());
            }
        }
    }

    public void handleClose(Listener dynamicUI, InventoryCloseEvent e) {
        if (e.getPlayer() == player) {
            buttons.clear();
            inventory.clear();
            HandlerList.unregisterAll(dynamicUI);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Integer> getBorderSlots() {
        return borderSlots;
    }

    public ItemStack getBoarderStack() {
        return boarderStack;
    }

}
