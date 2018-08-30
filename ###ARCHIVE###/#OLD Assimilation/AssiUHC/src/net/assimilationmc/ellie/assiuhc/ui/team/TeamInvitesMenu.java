package net.assimilationmc.ellie.assiuhc.ui.team;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.TeamManager;
import net.assimilationmc.ellie.assiuhc.games.util.UHCTeam;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Set;

/**
 * Created by Ellie on 18.7.17 for AssimilationMC.
 * Affiliated with www.minevelop.com
 */
public class TeamInvitesMenu extends DynamicUI implements Listener {

    private TeamManager teamManager;

    public TeamInvitesMenu(TeamManager teamManager, Player player){
        super(27, "&cPending Team Invites", player);
        this.teamManager = teamManager;
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        player.openInventory(this.build());
    }

    @Override
    public Inventory build() {
        Set<String> invites = teamManager.getPlayerInvites(getPlayer());
        if(invites == null || invites.isEmpty()){
            addButton(13, (player, type) -> {}, new ItemBuilder(Material.SKULL_ITEM).asPlayerHead("MrDerpling").setDisplay(UColorChart.R+"Nothing to see here.").build());
            return super.build();
        }


        int slot = 0;
        for (String invite : invites) {
            UHCTeam team = teamManager.getTeam(invite);
            if(team != null){
                addButton(slot, (player, type) -> {

                    if(type.isLeftClick()){
                        teamManager.acceptTeamInvite(team, player);
                    }else{
                        teamManager.rejectTeamInvite(team, player);
                    }
                    closeInventory();

                }, new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(team.getLeader()).setDisplay(UColorChart.VARIABLE+team.getLeader())
                        .setLore("&f",
                                UColorChart.R+"Team: "+team.getChatColor()+"&l"+team.getName(),
                                "&f",
                                UColorChart.R+"To &aaccept "+UColorChart.R+"left click.",
                                UColorChart.R+"To &cdecline "+UColorChart.R+"right click."
                                ).build());
                slot++;
            }

        }



        return super.build();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        handleClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        handleClose(this, e);
    }


}
