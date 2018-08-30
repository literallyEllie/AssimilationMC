package net.assimilationmc.uhclobbyadaptor.items.create.ui;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Callback;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.items.create.GameCreationConfiguration;
import net.assimilationmc.uhclobbyadaptor.lib.custom.CustomizationProperties;
import net.assimilationmc.uhclobbyadaptor.lib.custom.CustomizationProperty;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class GameCustomMenu extends UI implements Listener {

    private final UHCLobbyAdaptor lobbyAdaptor;

    private final Map<UUID, Callback<Integer>> inputCallbacks;

    public GameCustomMenu(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(uhcLobbyAdaptor.getAssiPlugin(), C.C + "Create " + C.SS + C.C + "... " + C.SS + C.II + "Game Settings", 18);
        this.lobbyAdaptor = uhcLobbyAdaptor;
        this.inputCallbacks = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, lobbyAdaptor);
    }

    public void open(GameCreationConfiguration creationConfiguration) {
        removeAllButtons();

        for (CustomizationProperty customizationProperty : CustomizationProperties.values()) {
            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    final ItemBuilder itemBuilder = new ItemBuilder(customizationProperty.getMaterial());
                    String name = StringUtils.capitalize(customizationProperty.id().toLowerCase().replace("_", " "));

                    final Object o = creationConfiguration.getCustomProperties().getOrDefault(customizationProperty.id(), customizationProperty.getDefaultValue
                            (creationConfiguration.getGameSubType()));
                    if (o instanceof Boolean) {
                        itemBuilder.setDisplay(((boolean) o ? ChatColor.GREEN.toString() : ChatColor.RED.toString()) + name);
                        itemBuilder.setLore(C.C, C.C + "Click to turn it " + ((boolean) o ? ChatColor.RED + "OFF" : ChatColor.GREEN + "ON"));
                    } else {
                        itemBuilder.setDisplay(ChatColor.GREEN + name)
                                .setLore(C.C, C.C + "Click to change value (" + C.V + ((int) o == -2 ? "Normal time" : (int) o == -1
                                        ? "No warmup" : o) + C.C + ")");
                    }

                    return itemBuilder.build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {

                    final Object o = creationConfiguration.getCustomProperties().getOrDefault(customizationProperty.id(), customizationProperty.getDefaultValue
                            (creationConfiguration.getGameSubType()));
                    if (o instanceof Boolean) {
                        if ((boolean) customizationProperty.getDefaultValue() == !(boolean) o) {
                            creationConfiguration.getCustomProperties().remove(customizationProperty.id());
                        } else creationConfiguration.addProperty(customizationProperty.id(), !((boolean) o));
                        open(creationConfiguration);
                        return;
                    }

                    assiPlayer.sendMessage(C.II + "Please type the new value.");
                    if (customizationProperty == CustomizationProperties.WARMUP_TIME) {
                        assiPlayer.sendMessage(C.C + "The time is in seconds: -1 to disable, -2 to reset");
                    } else assiPlayer.sendMessage(C.C + "Please use a value between 3 and 60 (Normal is 20).");

                    // D.d("put");
                    inputCallbacks.put(assiPlayer.getUuid(), integer -> {

                        if (customizationProperty == CustomizationProperties.MAX_HEALTH && (integer < 3 || integer > 60)) {
                            assiPlayer.sendMessage(C.II + "Please use a value between 3 and 60 (Normal is 20).");
                            return;
                        }

                        if (customizationProperty == CustomizationProperties.WARMUP_TIME) {
                            if (integer < 1) {
                                if (integer == 0) {
                                    integer = -1;
                                }
                                assiPlayer.sendMessage(C.II + "Warmup disabled.");
                            } else if (integer > 60 * 30) {
                                assiPlayer.sendMessage(C.II + "The warmup time cannot exceed 30 minutes.");
                                return;
                            }
                        }

                        inputCallbacks.remove(assiPlayer.getUuid());
                        creationConfiguration.addProperty(customizationProperty.id(), integer);
                        open(creationConfiguration);
                    });

                    // D.d("close");
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> closeInventory(assiPlayer.getBase()), 3L);

                }
            });

        }

        addButton(17, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.INK_SACK).setColor(ItemBuilder.StackColor.GREEN).setDisplay(C.II + "Create my game!").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                assiPlayer.sendMessage(ChatColor.GREEN + "Thank you for supporting AssimilationMC!");
                closeInventory(assiPlayer.getBase());
                lobbyAdaptor.getCreationFactory().create(creationConfiguration);
            }
        });

        super.open(creationConfiguration.getCreator());
    }

    @Override
    public void onClose(Player player) {
        if (!inputCallbacks.containsKey(player.getUniqueId())) {
            D.d("unregistering, closed off");
            HandlerList.unregisterAll(this);
        }
        super.onClose(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final AsyncPlayerChatEvent e) {
        if (!inputCallbacks.containsKey(e.getPlayer().getUniqueId())) return;

        e.setCancelled(true);

        final String trim = e.getMessage().trim();
        int val;
        try {
            val = Integer.parseInt(trim);
        } catch (NumberFormatException ex) {
            e.getPlayer().sendMessage(C.II + "Invalid number.");
            return;
        }

        inputCallbacks.get(e.getPlayer().getUniqueId()).callback(val);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerQuitEvent e) {
        inputCallbacks.remove(e.getPlayer().getUniqueId());
    }

}
