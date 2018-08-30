package net.assimilationmc.uhclobbyadaptor.items.create.ui;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.items.create.GameCreationConfiguration;
import net.assimilationmc.uhclobbyadaptor.lib.UHCSingledMaps;
import net.assimilationmc.uhclobbyadaptor.lib.UHCTeamedMaps;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.print.DocFlavor;

public class GameCreationMapMenu extends UI {

    private final UHCLobbyAdaptor lobbyAdaptor;

    public GameCreationMapMenu(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(uhcLobbyAdaptor.getAssiPlugin(), "", 36);
        this.lobbyAdaptor = uhcLobbyAdaptor;
    }

    public void open(GameCreationConfiguration creationConfiguration) {
        removeAllButtons();
        setInventoryTitle(C.C + "Create " + C.SS + C.C + creationConfiguration.getGameSubType().getDisplay() + C.SS + C.II + " Maps");

        if (creationConfiguration.getGameSubType().isTeamed()) {

            for (UHCTeamedMaps uhcTeamedMap : UHCTeamedMaps.values()) {
                if (!uhcTeamedMap.isEnabled() || !uhcTeamedMap.isApplicable(creationConfiguration.getGameSubType()))
                    continue;
                addButton(new Button() {
                    @Override
                    public ItemStack getItemStack(AssiPlayer assiPlayer) {
                        return uhcTeamedMap.getItemStack();
                    }

                    @Override
                    public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                        creationConfiguration.setMap(uhcTeamedMap.getId());

                        if (assiPlayer.getRank().isDonator() || assiPlayer.getRank().isPromoter() || assiPlayer.getRank().isHigherThanOrEqualTo(Rank.MOD)) {
                            GameCustomMenu customMenu = new GameCustomMenu(lobbyAdaptor);
                            customMenu.open(creationConfiguration);
                            assiPlayer.sendMessage(ChatColor.YELLOW + "As a supporter to the server, you have access to customize your game even more...");
                        } else {
                            closeInventory(assiPlayer.getBase());
                            assiPlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Did you know, as a donator you can customize your game even more?");
                            lobbyAdaptor.getCreationFactory().create(creationConfiguration);
                        }
                    }
                });
            }

        } else {

            for (UHCSingledMaps uhcSingledMap : UHCSingledMaps.values()) {
                if (!uhcSingledMap.isEnabled() || !uhcSingledMap.isApplicable(creationConfiguration.getGameSubType()))
                    continue;
                addButton(new Button() {
                    @Override
                    public ItemStack getItemStack(AssiPlayer assiPlayer) {
                        return uhcSingledMap.getItemStack();
                    }

                    @Override
                    public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                        creationConfiguration.setMap(uhcSingledMap.getId());

                        if (assiPlayer.getRank().isDonator() || assiPlayer.getRank().isPromoter() || assiPlayer.getRank().isHigherThanOrEqualTo(Rank.MOD)) {
                            GameCustomMenu customMenu = new GameCustomMenu(lobbyAdaptor);
                            customMenu.open(creationConfiguration);
                            assiPlayer.sendMessage(ChatColor.YELLOW + "As a supporter to the server, you have access to customize your game even more...");
                        } else {
                            closeInventory(assiPlayer.getBase());
                            assiPlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Did you know, as a donator you can customize your game even more?");
                            lobbyAdaptor.getCreationFactory().create(creationConfiguration);
                        }
                    }
                });
            }

        }

        this.open(creationConfiguration.getCreator());
    }

}
