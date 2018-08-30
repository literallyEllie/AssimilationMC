package net.assimilationmc.assicore.joinitems;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class JoinItemManager extends Module {

    private Map<Integer, JoinItem> joinItems;

    public JoinItemManager(AssiPlugin plugin) {
        super(plugin, "Join Item Manager");
    }

    @Override
    protected void start() {
        joinItems = Maps.newHashMap();
    }

    @Override
    protected void end() {
        joinItems.clear();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerJoinEvent e) {
        AssiPlayer player = getPlugin().getPlayerManager().getOnlinePlayers().get(e.getPlayer().getUniqueId());
        give(player);
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItem();
        if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta()) return;

        joinItems.values().stream().filter(joinItem -> joinItem.getItemStack().equals(itemStack)).forEach(joinItem -> {
            e.setCancelled(true);
            joinItem.onClick(player);
        });
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        joinItems.values().forEach(joinItem -> e.getPlayer().getInventory().remove(joinItem.getItemStack()));
    }

    public void give(AssiPlayer player) {
        for (JoinItem joinItem : joinItems.values()) {
            if (joinItem.hasGiveCondition() && !joinItem.getGiveCondition().onJoin(player)) continue;
            player.getBase().getInventory().setItem(joinItem.getSlot(), joinItem.getItemStack());
        }

    }

    public Map<Integer, JoinItem> getJoinItems() {
        return joinItems;
    }

    public void addItem(JoinItem joinItem) {
        if (joinItems.containsKey(joinItem.getSlot())) {
            removeItem(joinItem.getSlot());
        }
        joinItems.put(joinItem.getSlot(), joinItem);
        getPlugin().registerListener(joinItem);
    }

    public void removeItem(int slot) {
        joinItems.remove(slot);
    }

}
