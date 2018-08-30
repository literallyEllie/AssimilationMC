package net.assimilationmc.ellie.assiuhc.ui.team;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.IButton;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.TeamManager;
import net.assimilationmc.ellie.assiuhc.game.UHCTeam;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class TeamCreateMenu implements Listener {



    private class AnvilContainer extends ContainerAnvil {

        private String n;

        public AnvilContainer(EntityHuman entityHuman){
            super(entityHuman.inventory, entityHuman.world, new BlockPosition(0,0,0), entityHuman);
        }

        @Override
        public boolean a(EntityHuman entityHuman){
            return true;
        }

        @Override
        public void a(String origString){
            n = origString;
            itemName = origString;

            if (getSlot(2).hasItem()){
                net.minecraft.server.v1_8_R3.ItemStack itemstack = getSlot(2).getItem();

                if (StringUtils.isEmpty(origString)) {
                    itemstack.r();
                }
                else{
                    itemstack.c(this.n);
                }
            }
            e();
        }
    }

    private TeamManager teamManager;
    private Player player;
    private Inventory inventory;
    private HashMap<Integer, IButton> buttons;
    private String itemName = "";

    public TeamCreateMenu(TeamManager teamManager, Player player){
        this.teamManager = teamManager;
        this.player = player;
        this.buttons = new HashMap<>();
        build();
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer() == player){
            inventory.setItem(0, new org.bukkit.inventory.ItemStack(Material.AIR));
            inventory.setItem(2, new org.bukkit.inventory.ItemStack(Material.AIR));
            unregister();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if(e.getRawSlot() < 3) {

            e.setCancelled(true);

            if (e.getRawSlot() == 2) {

                if (itemName.length() > 1) {

                    String teamname = itemName.replace(" ", "");

                    if(teamname.length() < 3 || teamname.length() > 7){
                        org.bukkit.inventory.ItemStack itemStack = new ItemBuilder(org.bukkit.Material.DIAMOND_HELMET).setDisplay("").setLore("&cName too short/long").build();
                        inventory.setItem(2, itemStack);
                        return;
                    }

                    if(teamManager.getTeam(teamname) != null){
                        org.bukkit.inventory.ItemStack itemStack = new ItemBuilder(org.bukkit.Material.DIAMOND_HELMET).setDisplay("").setLore("&cTeam already exists").build();
                        inventory.setItem(2, itemStack);
                        return;
                    }

                    if(!teamManager.addTeam(teamname, player)){
                        Util.mWARN(player, "There was an error creating your team");
                        unregister();
                        return;
                    }
                    Util.mINFO(player, "You have created a team "+teamname);

                    final UHCTeam team = teamManager.getTeam(teamname);
                    teamManager.getGame().getScoreboard().updateTeams();

                    player.closeInventory();
                    unregister();


                    new TeamOptionsMenu(team, Bukkit.getPlayer(team.getLeader()));

                }
            }
        }

    }


    private void unregister(){
        HandlerList.unregisterAll(this);
        player = null;
    }

    private void build(){

        if(teamManager.getTeam(player) != null){
            Util.mINFO(player,"You already have a team.");
            return;
        }

        player.closeInventory();

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AnvilContainer container = new AnvilContainer(entityPlayer);
        int c = entityPlayer.nextContainerCounter();

        Packet packet = new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0);

        entityPlayer.playerConnection.sendPacket(packet);

        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.windowId = c;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);


        org.bukkit.inventory.ItemStack itemStack = new ItemBuilder(org.bukkit.Material.DIAMOND_HELMET).setDisplay("").setLore("&aType in the anvil to your team name",
                "&aIt must be between 3 and 7 characters").build();
        inventory = container.getBukkitView().getTopInventory();

        inventory.setItem(0, itemStack);
    }


}
