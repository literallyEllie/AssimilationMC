package net.assimilationmc.ellie.assiuhc.ui.team;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ItemLayout;
import net.assimilationmc.ellie.assicore.api.ui.IButton;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.TeamManager;
import net.assimilationmc.ellie.assiuhc.game.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ellie on 29/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class TeamInviteMenu implements Listener {

    private TeamManager teamManager;
    private Player player;
    private Inventory inventory;
    private HashMap<Integer, IButton> buttons;

    public TeamInviteMenu(TeamManager teamManager, Player player){
        this.teamManager = teamManager;
        this.player = player;
        this.buttons = new HashMap<>();
        player.closeInventory();
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        build();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer() == player){
            unregister();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getWhoClicked() != player) return;
        e.setCancelled(true);

        ItemStack itemStack = e.getCurrentItem();

        if(itemStack != null && itemStack.hasItemMeta() && itemStack.getType() == Material.SKULL_ITEM){

            ItemMeta itemMeta = itemStack.getItemMeta();

            String name = ChatColor.stripColor(itemMeta.getDisplayName());
            if(Bukkit.getPlayer(name) != null){

                UHCTeam team = teamManager.getTeam(player);
                Player player = Bukkit.getPlayer(name);



                if(!teamManager.getGame().getPlayers().contains(player.getName())){
                    Util.mINFO_noP(this.player, UHC.prefix+"&cPlayer is not in this game.");
                    return;
                }

                if(teamManager.hasTeam(player)){
                    Util.mINFO_noP(this.player, UHC.prefix+"&cPlayer is already in a team!");
                    return;
                }

                if(teamManager.isTeamFull(team)){
                    Util.mINFO_noP(this.player, UHC.prefix+"&cYour team is already full!");
                    return;
                }

                Util.mINFO_noP(this.player, UHC.prefix+"&cInvited &6"+player.getName()+"&7 to your team.");
                Util.mINFO_noP(player, UHC.prefix+"You have been invited to join the team &6"+teamManager.getTeam(this.player).getName());

            }else{
                Util.mINFO_noP(player, UHC.prefix+"&cPlayer was not found.");
            }



        }



    }

    private void build(){
        inventory = Bukkit.createInventory(null, 45, "Invite players");
        ArrayList<Integer> slots = new ItemLayout("XXXXXXXXX", "XOOOOOOOX", "XOOOOOOOX", "XOOOOOOOX", "XXXXXXXXX").getItemSlots();

        ArrayList<ItemStack> players = new ArrayList<>();
        teamManager.getGame().getPlayers().forEach(s -> {
            if(Bukkit.getPlayer(s) != null && teamManager.getTeam(Bukkit.getPlayer(s)) == null)
                players.add(new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(s).setDisplay("&a"+s).setLore("&f", "&7Click this head to invite them to your team", "&f").build());
        });

        final boolean a = players.isEmpty();

        for (int i = 0; i < inventory.getSize(); i++) {
            if(slots.contains(i)){
                try {
                    inventory.setItem(i, players.get(i-10));
                }catch(IndexOutOfBoundsException e){}
                continue;
            }
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.ORANGE).setDisplay("&f").build());
        }

        if(a){
            inventory.setItem(22, new ItemBuilder(Material.SKULL_ITEM).asPlayerHead("MrDerpling").setDisplay("&fder is no one here :(").build());
        }

        player.openInventory(inventory);
    }

    private void unregister(){
        HandlerList.unregisterAll(this);
        player = null;
    }


}
