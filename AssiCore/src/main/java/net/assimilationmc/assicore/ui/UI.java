package net.assimilationmc.assicore.ui;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.Map;
import java.util.UUID;

public class UI {

    private final AssiPlugin plugin;
    private final int inventorySize;
    private final Map<Integer, Button> buttonMap;
    private final Map<UUID, Inventory> viewers;
    private final ItemStack filler;
    private String inventoryTitle;

    public UI(AssiPlugin plugin, String inventoryName, int inventorySize) {
        this.plugin = plugin;
        this.plugin.getUiManager().registerUI(this);

        this.inventoryTitle = inventoryName;
        this.inventorySize = inventorySize;

        this.buttonMap = Maps.newHashMap();
        this.viewers = Maps.newHashMap();

        this.filler = new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.ORANGE).setDisplay(C.C).build();
    }

    public UI(AssiPlugin plugin, String inventoryName, RowSize rowSize) {
        this(plugin, inventoryName, rowSize.getSlots());
    }

    protected AssiPlugin getPlugin() {
        return plugin;
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public void setInventoryTitle(String inventoryTitle) {
        this.inventoryTitle = inventoryTitle;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public Map<Integer, Button> getButtonMap() {
        return buttonMap;
    }

    public Button getButton(int slot) {
        return buttonMap.get(slot);
    }

    public boolean isButton(int slot) {
        return buttonMap.containsKey(slot);
    }

    public void addButton(int slot, Button button) {
        if (slot >= inventorySize) return;
        buttonMap.put(slot, button);
    }

    public void addButton(Button button) {
        for (int i = 0; i < inventorySize; i++) {
            if (!buttonMap.containsKey(i)) {
                buttonMap.put(i, button);
                break;
            }
        }
    }

    public void removeButton(int slot) {
        buttonMap.remove(slot);
    }

    public void removeAllButtons() {
        buttonMap.clear();
    }

    public Map<UUID, Inventory> getViewers() {
        return viewers;
    }

    public void open(Player player) {

        if (player.getOpenInventory() != null) {
            player.closeInventory();
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                Inventory inventory = build(player);
                viewers.put(player.getUniqueId(), inventory);
            }, 1L);
            return;
        }
        Inventory inventory = build(player);
        viewers.put(player.getUniqueId(), inventory);
    }

    private Inventory build(Player player) {
        if (viewers.containsKey(player.getUniqueId())) {
            Inventory inventory = viewers.get(player.getUniqueId());
            inventory.clear();
            viewers.remove(player.getUniqueId());
        }

        final Inventory inventory = plugin.getServer().createInventory(null, this.inventorySize, this.inventoryTitle);

        this.buttonMap.forEach((integer, button) -> inventory.setItem(integer, button.getItemStack(plugin.getPlayerManager().getPlayer(player))));
        plugin.getUiManager().expireInventorySession(this, player.getUniqueId());

        player.openInventory(inventory);

        return inventory;
    }

    public void destroySelf(boolean rem) {
        buttonMap.clear();

        for (UUID uuid : viewers.keySet()) {
            Player player = UtilPlayer.get(uuid);
            if (player != null && player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null && player.getOpenInventory()
                    .getTopInventory().getName().equals(this.inventoryTitle)) {
                closeInventory(player);
            }
        }

        viewers.clear();

        if (rem) plugin.getUiManager().unregisterUI(this);
    }

    public void destroySelf() {
        this.destroySelf(true);
    }

    public void closeInventory(Player player) {
        try {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player != null && player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null && player.getOpenInventory()
                        .getTopInventory().getName().equals(this.inventoryTitle)) {
                    player.closeInventory();
                }
            }, 1L);
        } catch (IllegalPluginAccessException e) {
            player.closeInventory();
        }
    }

    public void onClose(Player player) {
        this.viewers.remove(player.getUniqueId());
    }

    public boolean isOpen(Player player) {
        return this.viewers.containsKey(player.getUniqueId());
    }

    public ItemStack getFiller() {
        return filler;
    }
}
