package net.assimilationmc.ellie.assiuhc.ui;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.TeamManager;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Created by Ellie on 14/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GamePlayerSelectorMenu extends DynamicUI implements Listener {

    private UHCGame game;
    private boolean teams;

    public GamePlayerSelectorMenu(Player player, UHCGame game){
        super(54, "&cPlayer Selector", player);
        this.game = game;
        this.teams = game.getTeamManager() != null;
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        player.openInventory(build());
    }

    @Override
    public Inventory build() {

        List<String> players = game.getPlayers();
        ItemBuilder playerBase = new ItemBuilder(Material.SKULL_ITEM);
        int slot = 0;
        for (String strPl : players) {
            Player player = Bukkit.getPlayer(strPl);
            if (player == null) continue;
            playerBase.asPlayerHead(player.getName()).setDisplay(getPlayerColor(player) + player.getName()).appendLore("&f");

            if (teams) {
                UHCTeam team = game.getTeamManager().getTeam(player);
                if (team != null) {
                    playerBase.setLore("&9Team: &f" + team.getName(),
                            "&9Members: " + team.getMembers().size(),
                            "&9Alive: " + team.getAlive().size(), "&f");
                }
            }
            playerBase.appendLore("&cClick to teleport!");

            addButton(slot, (player1, type) -> {
                Util.mINFO_noP(getPlayer(), "&cYou are now spectating &f" + player.getName());
                getPlayer().teleport(player);
                closeInventory();
            }, playerBase.build());

            slot++;
        }

        return super.build();
    }

    private String getPlayerColor(Player player) {
        if (teams) {
            TeamManager teamManager = game.getTeamManager();
            UHCTeam team = teamManager.getTeam(player);
            if (team != null) {
                return "&" + team.getTeamColor().getChatColor().getChar();
            }
        }
        return "&c";
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
