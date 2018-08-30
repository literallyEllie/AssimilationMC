package net.assimilationmc.uhclobbyadaptor.items;

import net.assimilationmc.assicore.joinitems.JoinItem;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilTime;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.items.create.ui.GameCreationMapMenu;
import net.assimilationmc.uhclobbyadaptor.items.create.ui.GameCreationSinglesMenu;
import net.assimilationmc.uhclobbyadaptor.items.create.ui.GameCreationTeamsMenu;
import net.assimilationmc.uhclobbyadaptor.stats.UHCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GameCreateItem extends JoinItem implements Listener {

    private final UHCLobbyAdaptor lobbyAdaptor;
    private UI initMenu, gameCreationTeamMenu, gameCreationSinglesMenu;

    public GameCreateItem(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(3, new ItemBuilder(Material.EMERALD_BLOCK).setDisplay(ChatColor.GREEN + ChatColor.BOLD.toString() + "Create a UHC Game").build());
        this.lobbyAdaptor = uhcLobbyAdaptor;

        this.initMenu = new UI(uhcLobbyAdaptor.getAssiPlugin(), C.II + "Create a game", 27);
        this.initMenu.addButton(11, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                if (assiPlayer.getPunishProfile().getActivePunishments().containsKey(PunishmentCategory.BAD_UHC_TEAM_NAME)) {
                    return new ItemBuilder(Material.BARRIER).setDisplay(C.C + "Teams").setLore(C.C, C.II + "You are not currently permitted to play team games.", C.C)
                            .build();
                }
                // TODO also check timeouts

                UHCPlayer uhcPlayer = lobbyAdaptor.getROuhcStatsProvider().getPlayer(assiPlayer.getBase());

                if (uhcPlayer.getCooldownData().isActive()) {
                    return new ItemBuilder(Material.BARRIER).setDisplay(C.C + "Teams").setLore(C.C, C.II + "You cannot play team games until",
                            C.V + UtilTime.formatTimeStamp(uhcPlayer.getCooldownData().getRemaining())).build();
                }

                return new ItemBuilder(Material.WATCH).setDisplay(C.C + "Teams").setLore(C.C, C.V + "Work together to win!", C.C, C.II +
                        "Click this to create a Teamed game.").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                if (assiPlayer.getPunishProfile().getActivePunishments().containsKey(PunishmentCategory.BAD_UHC_TEAM_NAME)
                        || lobbyAdaptor.getROuhcStatsProvider().getPlayer(assiPlayer.getBase()).getCooldownData().isActive()) {
                    assiPlayer.getBase().playSound(assiPlayer.getBase().getLocation(), Sound.ANVIL_LAND, 3F, .5f);
                    return;
                }

                gameCreationTeamMenu.open(assiPlayer.getBase());
            }
        });

        this.initMenu.addButton(15, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.COMPASS).setDisplay(C.C + "Singles").setLore(C.C, C.V + "Play solo against other players", C.C, C.II +
                        "Click this to create a Singles game.").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                gameCreationSinglesMenu.open(assiPlayer.getBase());
            }
        });

        this.initMenu.addButton(13, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.JUKEBOX).setDisplay(C.C + "Random").setLore(C.C, C.II + "Click this to choose a random option!").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                if (assiPlayer.getPunishProfile().getActivePunishments().containsKey(PunishmentCategory.BAD_UHC_TEAM_NAME)
                        || lobbyAdaptor.getROuhcStatsProvider().getPlayer(assiPlayer.getBase()).getCooldownData().isActive()) {
                    gameCreationSinglesMenu.open(assiPlayer.getBase());
                    return;
                }

                switch (new Random().nextInt(2)) {
                    case 1:
                        gameCreationSinglesMenu.open(assiPlayer.getBase());
                    default:
                        gameCreationTeamMenu.open(assiPlayer.getBase());
                }
            }
        });

        this.initMenu.addButton(26, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.II + "Quit").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                initMenu.closeInventory(assiPlayer.getBase());
            }
        });

        GameCreationMapMenu gameCreationMapMenu = new GameCreationMapMenu(uhcLobbyAdaptor);

        this.gameCreationTeamMenu = new GameCreationTeamsMenu(initMenu, gameCreationMapMenu, uhcLobbyAdaptor.getAssiPlugin());
        this.gameCreationSinglesMenu = new GameCreationSinglesMenu(initMenu, gameCreationMapMenu, uhcLobbyAdaptor.getAssiPlugin());

        setGiveCondition(assiPlayer -> lobbyAdaptor.getAssiPlugin().getWebAPIManager().isEnabled() && lobbyAdaptor.getServerMap().size() < 20);
    }

    @Override
    public void onClick(Player player) {

        if (lobbyAdaptor.getCreationFactory().canMake(player)) {
            initMenu.open(player);
        } else player.sendMessage(C.II + "You cannot do this right now.");
    }

}
