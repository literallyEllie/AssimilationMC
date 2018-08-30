package net.assimilationmc.assicore.lobby;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.internal.InternalPingHandle;
import net.assimilationmc.assicore.internal.ServerPing;
import net.assimilationmc.assicore.joinitems.JoinItem;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilBungee;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemLobbySelector extends JoinItem implements Listener {

    private AssiPlugin plugin;
    private InternalPingHandle pingHandle;
    private UI ui;
    private Map<String, ServerPing> servers;

    public ItemLobbySelector(AssiPlugin assiPlugin) {
        super(7, new ItemBuilder(Material.BED).setDisplay(ChatColor.AQUA + ChatColor.BOLD.toString() + "Lobby Selector").build());
        this.plugin = assiPlugin;

        ui = new UI(assiPlugin, C.C + "Lobby Selector", 18);
        servers = Maps.newHashMap();
        this.pingHandle = assiPlugin.getInternalPingHandle();
        plugin.getServer().getScheduler().runTaskLater(plugin, this::ping, 80L);
    }

    @Override
    public void onClick(Player player) {
        ui.removeAllButtons();

        for (Map.Entry<String, ServerPing> stringStringEntry : servers.entrySet()) {
            String serverId = stringStringEntry.getKey();
            ServerPing ping = stringStringEntry.getValue();

            int players = Integer.valueOf(ping.getAttribute("online"));
            int maxPlayers = Integer.parseInt(ping.getAttribute("max_players"));

            ui.addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return (players == 0 ? new ItemBuilder(Material.COAL_BLOCK) : new ItemBuilder(Material.REDSTONE_BLOCK)).setDisplay(C.II + serverId).
                            setLore("", C.C + "Players: " + C.V + players + C.C + "/" + C.V + maxPlayers, "").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                    ui.closeInventory(clicker.getBase());
                    UtilBungee.sendPlayer(plugin, player, serverId);
                }
            });
        }

        ui.open(player);
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() == UpdateType.MIN) {
            ping();
        }
    }

    private void ping() {
        servers.clear();
        int id = pingHandle.ping("LOBBY", data -> servers.put(data.getServerId(), data));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> pingHandle.unregisterCallback(id), 120 * 2);
    }

}
