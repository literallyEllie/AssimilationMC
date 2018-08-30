package net.assimilationmc.ellie.assiuhc.ui;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assiuhc.UHC;
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
public class GameJoinMenu extends DynamicUI implements Listener {

    public GameJoinMenu(Player player){
        super(27, "            &2Game Selector", player, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17,
                18,19,20,21,22,23,24,25), new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.LIME).setDisplay("&f").build());
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        player.openInventory(this.build());
    }

    @Override
    public Inventory build() {

        addButton(11, ((player, type) -> {
            closeInventory();
            new GameSelectorTeamMenu(player);
        }), new ItemBuilder(Material.WATCH).setDisplay("&cTeams")
                .setLore("&f", "&9Work together to win!").build());

        addButton(13, ((player, type) -> {
            closeInventory();
            new GameSelectorSpectateMenu(getPlayer());
        }), new ItemBuilder(Material.MAP).setDisplay("&cSpectate").setLore("&f", "&9Spectate a game").build());

        addButton(15, ((player, type) -> {
            closeInventory();
            new GameSelectorSinglesMenu(getPlayer());
        }), new ItemBuilder(Material.COMPASS).setDisplay("&cSingles")
        .setLore("&f", "&9Can you survive on your own?").build());

        addButton(26, ((player, type) -> closeInventory()), new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cQuit").build());

        return super.build();
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
