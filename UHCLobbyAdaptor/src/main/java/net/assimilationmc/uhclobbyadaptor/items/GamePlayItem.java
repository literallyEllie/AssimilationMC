package net.assimilationmc.uhclobbyadaptor.items;

import net.assimilationmc.assicore.joinitems.JoinItem;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.UHCServer;
import net.assimilationmc.uhclobbyadaptor.lib.custom.CustomizationProperties;
import net.assimilationmc.uhclobbyadaptor.stats.UHCPlayer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class GamePlayItem extends JoinItem implements Listener {

    private UHCLobbyAdaptor lobbyAdaptor;
    private UI ui;

    public GamePlayItem(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(5, new ItemBuilder(Material.DIAMOND_SWORD).setDisplay(C.II + ChatColor.BOLD + "Play UHC").build());
        this.lobbyAdaptor = uhcLobbyAdaptor;
        this.ui = new UI(uhcLobbyAdaptor.getAssiPlugin(), C.II + "UHC Game Selector", 54);
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        e.getPlayer().getInventory().setHeldItemSlot(getSlot());
    }

    @Override
    public void onClick(Player player) {

        if (!lobbyAdaptor.getCreationFactory().canMake(player)) {
            player.sendMessage(C.II + "You cannot do this right now.");
            return;
        }

        ui.removeAllButtons();

        if (lobbyAdaptor.getServerMap().isEmpty() || lobbyAdaptor.getServerMap().values().stream().
                filter(uhcServer -> uhcServer.getGamePhase().equals("END")).count() == lobbyAdaptor.getServerMap().size()) {

            ui.addButton(22, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return new ItemBuilder(Material.EMERALD_BLOCK).setDisplay(C.II + "There are no available games to play!")
                            .setLore("", C.II + "Why wait for a lobby when you can make a lobby yourself?",
                                    C.V + "Click here to start!").build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                    ui.closeInventory(player);
                    lobbyAdaptor.getGameCreateItem().onClick(player);
                }
            });

            ui.open(player);
            return;
        }

        Party senderParty = lobbyAdaptor.getAssiPlugin().getPartyManager().getPartyOf(player, false);

        for (UHCServer uhcServer : lobbyAdaptor.getServerMap().values()) {
            if (uhcServer.getGamePhase().equals("END")) continue;

            // TODO check if team or not

            ui.addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    final ItemBuilder itemBuilder = (uhcServer.getGamePhase().equals("LOBBY") ? new ItemBuilder(uhcServer.getGameSubType().getItem().clone())
                            .setDisplay(ChatColor.GREEN + uhcServer.getServerId()) :
                            new ItemBuilder(Material.IRON_BLOCK).setDisplay(ChatColor.RED + uhcServer.getServerId())
                                    .setLore("", C.II + "This game has already started."))
                            .setLore("", C.C + "Game-mode: " + C.V + uhcServer.getGameSubType().getDisplay(),
                                    C.C + "Game-Phase: " + C.V + StringUtils.capitalize(uhcServer.getGamePhase().toLowerCase().replace("_", " ")),
                                    C.C + "Players: " + C.V + uhcServer.getOnline() + C.C + "/" + C.V + uhcServer.getMaxPlayers(),
                                    C.C + "Map: " + C.V + uhcServer.getMapName(),
                                    (uhcServer.getWarmupStart() == 0 ? C.C : C.C + "This game started " + C.V + UtilTime.formatTimeStamp(uhcServer.getWarmupStart())));

                    if (uhcServer.hasCustom()) {
                        if (uhcServer.getWarmupStart() != 0) {
                            itemBuilder.setLore(C.C);
                        }
                        itemBuilder.setLore(C.II + "Custom values: ");

                        for (Map.Entry<String, Object> stringObjectEntry : uhcServer.getCustomAttributes().entrySet()) {
                            String id = StringUtils.capitalize(stringObjectEntry.getKey().toLowerCase().replace("_", " "));
                            Object value = stringObjectEntry.getValue();

                            String displayValue = String.valueOf(value);
                            if (value instanceof Boolean) {
                                displayValue = displayValue.replace("true", ChatColor.GREEN + "Enabled").replace("false", C.II + "Disabled");
                            }

                            if (id.equals("Warmup time")) {
                                id = "Warmup time (s)";

                                if ((double) value < 1) {
                                    displayValue = "Disabled";
                                }

                            }

                            itemBuilder.setLore(C.C + id + ": " + C.V + displayValue);
                        }
                    }

                    return itemBuilder.build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {

//                    if (uhcServer.getGameSubType() == UHCGameSubType.SINGLES) {
//                        UHCPlayer uhcPlayer = lobbyAdaptor.getROuhcStatsProvider().getPlayer(assiPlayer.getBase());
//
//                        if (uhcPlayer.getCooldownData().isActive()) {
//                            assiPlayer.sendMessage(C.II + ChatColor.BOLD + "You cannot play competitive matches until " +
//                                    UtilTime.formatTimeStamp(uhcPlayer.getCooldownData().getRemaining()));
//                            ui.closeInventory(assiPlayer.getBase());
//                            return;
//                        }
//
//                    }

                    if (senderParty != null) {
                        ui.closeInventory(assiPlayer.getBase());
                        for (UUID uuid : senderParty.getMembers()) {
                            if (uuid == assiPlayer.getUuid()) continue;
                            Player bPlayer = UtilPlayer.get(uuid);
                            UHCPlayer tPlayer = lobbyAdaptor.getROuhcStatsProvider().getPlayer(bPlayer);

                            if (tPlayer.getCooldownData().isActive()) {
                                assiPlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "You cannot play competitive matches until " +
                                        UtilTime.formatTimeStamp(tPlayer.getCooldownData().getRemaining()));

                                senderParty.messageParty(C.V + ChatColor.BOLD + tPlayer.getName() + C.II + ChatColor.BOLD + " cannot play competitive matches.");
                                return;
                            }

                        }

                        lobbyAdaptor.getAssiPlugin().getPartyManager().setTarget(senderParty, uhcServer.getServerId());
                    }

                    ui.closeInventory(assiPlayer.getBase());
                    UtilBungee.sendPlayer(lobbyAdaptor.getAssiPlugin(), player, uhcServer.getServerId());
                }
            });
        }

        ui.open(player);
    }

}
