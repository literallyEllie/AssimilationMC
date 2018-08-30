package net.assimilationmc.assicore.world;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Callback;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LocationSelector implements Listener {

    private final Player selector;
    private final ItemStack selectorItem;
    private Location point1;
    private Location point2;
    private Callback<Pair<Location, Location>> onFinish;

    public LocationSelector(AssiPlugin plugin, Player player, Callback<Pair<Location, Location>> onFinish) {
        plugin.registerListener(this);
        this.selector = player;
        this.selectorItem = new ItemBuilder(Material.STICK).setDisplay(C.C + "Location Selector").build();
        this.onFinish = onFinish;
        player.getInventory().addItem(selectorItem);

        player.sendMessage(C.II + "See the " + C.V + "STICK" + C.II + " in your inventory, it is your Location Selector.");
        player.sendMessage(C.V + "Right-Click" + C.C + " to select Point 1.");
        player.sendMessage(C.V + "Left-Click" + C.C + " to select Point 2.");
        player.sendMessage(C.C + "When selecting a point you will get a message, if you don't receive it, the point hasn't been set.");
    }

    public Player getSelector() {
        return selector;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public boolean finished() {
        return point1 != null && point2 != null;
    }

    private void onFinish() {
        HandlerList.unregisterAll(this);
        selector.getInventory().remove(selectorItem);
        selector.sendMessage(C.C + "Done!");
        onFinish.callback(new ImmutablePair<>(point1, point2));
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (!player.equals(selector)) return;
        final ItemStack holding = e.getItem();
        if (holding == null || !holding.equals(selectorItem)) return;

        switch (e.getAction()) {
            case RIGHT_CLICK_BLOCK:
                player.sendMessage(C.V + "Point 1" + C.C + " set to the clicked block position.");
                point1 = e.getClickedBlock().getLocation();
                break;
            case LEFT_CLICK_BLOCK:
                player.sendMessage(C.V + "Point 2" + C.C + " set to the clicked block position.");
                point2 = e.getClickedBlock().getLocation();
                break;
            default:
                return;
        }

        if (finished()) {
            player.sendMessage(C.C + "Both of your positions have now been defined. Type \"done\" to confirm.");
        }

        e.setCancelled(true);

    }

    @EventHandler
    public void on(final AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!player.equals(selector)) return;
        if (!e.getMessage().equalsIgnoreCase("done")) return;

        e.setCancelled(true);

        if (!finished()) {
            player.sendMessage(C.II + "You haven't defined one or any points.");
            return;
        }

        onFinish();
    }

}
