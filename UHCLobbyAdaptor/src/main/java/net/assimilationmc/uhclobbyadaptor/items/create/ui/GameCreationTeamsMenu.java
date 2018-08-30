package net.assimilationmc.uhclobbyadaptor.items.create.ui;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.items.create.GameCreationConfiguration;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GameCreationTeamsMenu extends UI {

    public GameCreationTeamsMenu(UI previousMenu, GameCreationMapMenu creationMapMenu, AssiPlugin plugin) {
        super(plugin, C.C + "Create" + C.SS + C.II + "Teams", 9);

        this.addButton(8, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.II + "Back").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                previousMenu.open(assiPlayer.getBase());
            }
        });

        for (UHCGameSubType uhcGameSubType : UHCGameSubType.values()) {
            if (!uhcGameSubType.isTeamed()) continue;

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return uhcGameSubType.getItem();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                    GameCreationConfiguration creationConfiguration = new GameCreationConfiguration(assiPlayer.getBase());
                    creationConfiguration.setGameSubType(uhcGameSubType);
                    creationMapMenu.open(creationConfiguration);
                }
            });
        }

    }

    @Override
    public void open(Player player) {
        AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);
        if (assiPlayer.getPunishProfile().getActivePunishments().containsKey(PunishmentCategory.BAD_UHC_TEAM_NAME)
                || UHCLobbyAdaptor.getPlugin(UHCLobbyAdaptor.class).getROuhcStatsProvider().getPlayer(assiPlayer.getBase()).getCooldownData().isActive()) {
            closeInventory(player);
            return;
        }

        super.open(player);
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
    }
}
