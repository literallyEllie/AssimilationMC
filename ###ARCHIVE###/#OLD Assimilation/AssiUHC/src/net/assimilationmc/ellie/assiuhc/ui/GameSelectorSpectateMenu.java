package net.assimilationmc.ellie.assiuhc.ui;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

/**
 * Created by Ellie on 27/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameSelectorSpectateMenu extends DynamicUI implements Listener {

    public GameSelectorSpectateMenu(Player player){
        super(54, "     &2Spectator Game Selector", player, Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 17,
                18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52), new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.LIME)
                .setDisplay("&f").build());
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        player.openInventory(this.build());
    }


    @Override
    public Inventory build() {
        Inventory inventory = super.build();

        int index = 0;

        addButton(53, (player1, type) -> {
            closeInventory();
            new GameJoinMenu(getPlayer());
        }, new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cBack").build());

        Object[] uhcGames = UHC.getPlugin(UHC.class).getGameManager().getGames().values().toArray();

        addButton(4, (player1, type) -> {
        }, new ItemBuilder(Material.MAP).setDisplay("&cSpectate Game Selector")
                .setLore("&f", "&f&l"+uhcGames.length+" &9games available").build());


        if(uhcGames.length == 0){
            addButton(22, (player1, type) -> {
            }, new ItemBuilder(Material.SKULL_ITEM).asPlayerHead("MrDerpling").setDisplay("&fder is no games 2 watch :(").build());

            addButton(31, (player, type) -> {
                closeInventory();
                new GameCreateMenu(player);
            }, new ItemBuilder(Material.EMERALD_BLOCK).setDisplay("&aDIY")
                    .setLore("&7", "&9Why wait for a game to watch when you can make a lobby yourself?",
                            "&7Click here to start!").build());

            return inventory;
        }

        for (int i = 10; i < 42 && index < uhcGames.length; i++) {
            if(isButton(i)) continue;
            UHCGame game = (UHCGame) uhcGames[index];

            boolean teamed = game.getMap().isForTeams();

            addButton(i, ((player1, type) -> {
                closeInventory();
                UHC.getPlugin(UHC.class).getGameManager().joinSpectator(getPlayer(), game);
            }), new ItemBuilder(teamed ?game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed())
                    .getItemStack():game.getMap().getSingledGameType().get(game.getMap().getSelectedSingled()).getItemStack())
                    .setLore("&f", "&9&lID &f"+game.getId(),
                            "&9&lMap &f"+game.getMap().getName(),
                            "&9&lGame type &f"+(teamed?game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).name()
                                    :game.getMap().getSingledGameType().get(game.getMap().getSelectedTeamed()).name()),
                            "&9&lGame state &f"+game.getGameState().toString(),
                            "&9&lPlayers &f"+game.getPlayers().size()+"&7/&f"+game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).
                                    getMaxPlayers(),
                            "&9&lTeams &f"+(teamed?game.getTeamManager().getTeams().size()+"&7/&f"+game.getMap().getTeamedGameTypes()
                                    .get(game.getMap().getSelectedTeamed()).getMinTeams():"no teams"))
                    .build());
            index++;
        }


        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        handleClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        handleClose(this, e);
    }

}
