package net.assimilationmc.assicore.ui;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class UIManager extends Module {

    private Set<UI> uis;

    public UIManager(AssiPlugin plugin) {
        super(plugin, "UI Manager");
    }

    @Override
    protected void start() {
        uis = Sets.newConcurrentHashSet();
    }

    @Override
    protected void end() {
        uis.forEach(ui -> ui.destroySelf(false));
        uis.clear();
    }

    void registerUI(UI ui) {
        uis.add(ui);
    }

    void unregisterUI(UI ui) {
        this.uis.remove(ui);
    }

    public void expireInventorySession(UI newInventory, UUID uuid) {
        for (UI ui : uis) {
            if ((newInventory != null && ui != newInventory))
                ui.getViewers().remove(uuid);
        }
    }

    public UI getMenu(Player player) {
        for (UI ui : uis) {
            if (ui.getViewers().containsKey(player.getUniqueId()))
                return ui;
        }

        return null;
    }

    @EventHandler
    public void on(final InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player && e.getCurrentItem() != null && e.getInventory() != null && e.getInventory().getType() == InventoryType.CHEST))
            return;
        final Player player = (Player) e.getWhoClicked();
        final int rawSlot = e.getRawSlot();

        for (UI ui : uis) {
            if (!ui.getViewers().containsKey(player.getUniqueId())) continue;
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) continue;

            if (ui.isButton(rawSlot)) {
                ui.getButton(rawSlot).onAction(getPlugin().getPlayerManager().getPlayer(player), e.getClick());
            }

        }

    }

    @EventHandler
    public void on(final InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        final Player player = (Player) e.getPlayer();

        UI ui = getMenu(player);
        if (ui != null) {
            ui.onClose(player);
        }
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        UI ui = getMenu(player);
        if (ui != null) {
            ui.onClose(player);
        }
    }

}
