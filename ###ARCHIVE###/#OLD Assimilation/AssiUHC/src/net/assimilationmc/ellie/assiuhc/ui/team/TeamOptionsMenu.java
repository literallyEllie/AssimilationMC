package net.assimilationmc.ellie.assiuhc.ui.team;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ItemLayout;
import net.assimilationmc.ellie.assicore.api.ui.IButton;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.UHCTeam;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class TeamOptionsMenu implements Listener {

    private UHCTeam team;
    private Player player;
    private Inventory inventory;
    private HashMap<Integer, IButton> buttons;
    private TeamMenu currentPage = TeamMenu.OPTIONS;
    private TeamMenu previousPage;

    public TeamOptionsMenu(UHCTeam team, Player player){
        this.team = team;
        this.player = player;
        this.buttons = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        buildPage();
    }

    private void unregister(){
        HandlerList.unregisterAll(this);
        buttons.clear();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(inventory.getTitle().equals(e.getInventory().getTitle()) && e.getPlayer().equals(player)){
            unregister();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (inventory.getTitle().equals(e.getInventory().getTitle()) && e.getWhoClicked().equals(player)) {
            if (buttons.containsKey(e.getRawSlot())) {

                if (e.getWhoClicked() instanceof Player) {

                    IButton button = buttons.get(e.getRawSlot());
                    button.onClick(player, e.getClick());

                }
                e.setCancelled(true);
            }
        }
    }

    private void addButton(int slot, ItemStack item, IButton button){
        inventory.setItem(slot, item);
        buttons.put(slot, button);
    }

    private void buildPage(){

        inventory = Bukkit.createInventory(null, 27, currentPage.getName());

        buttons.clear();

        List<Integer> slots = Arrays.asList(10, 11, 12, 13, 14, 15, 16);

        for (int i = 0; i < TeamMenu.values().length; i++) {

            final TeamMenu menu = TeamMenu.values()[i];

            ItemStack is = menu.getItemStack();

            if(!team.getLeader().equals(player.getName())){
                if(menu == TeamMenu.INVITE_PLAYER)
                    continue;
            }

            if(menu == TeamMenu.OPTIONS){
                is = new ItemBuilder(menu.getItemStack()).setLore("&f", "").build();
            }

            if(menu == TeamMenu.LEADER){
                is = new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(team.getLeader()).setDisplay("&a"+team.getLeader()).setLore("&f", "&7The team leader").build();
            }

            if(menu == TeamMenu.TEAM_COLOR){
                if(team.getLeader().equals(player.getName()))
                    is = new ItemBuilder(Material.WOOL).setColor(team.getTeamColor()).setDisplay("&fTeam color").setLore("&f", "&7Set the team colour").build();
                else
                    is = new ItemBuilder(Material.WOOL).setColor(team.getTeamColor()).setDisplay("&fYour team color").build();
            }

            addButton(slots.get(i), is, (player, type) -> {
                switch (menu){
                    case LEADER:
                        break;
                    default:
                        if(currentPage != menu){
                            currentPage = menu;
                            buildPage();
                        }
                        break;
                }
            });
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null || (inventory.getItem(i) != null && inventory.getItem(i).getType() == Material.AIR)){
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.ORANGE).build());
            }
        }

        switch (currentPage){


            case OPTIONS:
                //build options
                break;
            case LEADER:
                break;
            case TEAM_COLOR:
                this.buildTeamColor();
                break;
            case MEMBERS:
                buildMembers();
                break;
            case INVITE_PLAYER:
                player.closeInventory();
                unregister();
                new TeamInviteMenu(team.getTeamManager(), player);
                return;
            default:
                break;
        }

        if(previousPage != currentPage){
            previousPage = currentPage;

            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

            if(entityPlayer.activeContainer != entityPlayer.defaultContainer){
                CraftEventFactory.handleInventoryCloseEvent(entityPlayer);
                entityPlayer.m();
            }
            player.openInventory(inventory);
        }


    }

    private void buildTeamColor(){

        ArrayList<Integer> slots = new ItemLayout("XXOOXOOXX", "XOOOXOOOX").getItemSlots();

        ItemBuilder.StackColor[] colors = {ItemBuilder.StackColor.ORANGE, ItemBuilder.StackColor.LIGHT_BLUE, ItemBuilder.StackColor.YELLOW,
                ItemBuilder.StackColor.LIME, ItemBuilder.StackColor.PINK, ItemBuilder.StackColor.CYAN, ItemBuilder.StackColor.PURPLE,
                ItemBuilder.StackColor.BLUE, ItemBuilder.StackColor.GREEN, ItemBuilder.StackColor.RED };

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        for (int i = 0; i < colors.length; i++) {

            ItemBuilder.StackColor color = colors[i];

            if (team.getTeamManager().getTakenTeamColors().contains(color)) {
                addButton(slots.get(i), new ItemBuilder(Material.BARRIER).setDisplay(color.getChatColor()+color.name().toLowerCase())
                        .setLore("&7", "&cThis color has already been taken.").build(), (player, type) -> {
                        });
                continue;
            }

            ItemStack is = new ItemBuilder(Material.WOOL).setColor(color)
                    .setDisplay(color.getChatColor() + color.name().toLowerCase()).setLore("&7", "&7Click this to set your team color to " + color.getChatColor() + color.name().toLowerCase()).build();

            final int slot = i;

            addButton(slots.get(i), is, (player, type) -> {
                team.setTeamColor(color);
                team.getTeamManager().getGame().getScoreboard().updateTeams();
                currentPage = TeamMenu.OPTIONS;
                buttons.remove(slot);
                buildPage();
            });
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null || (inventory.getItem(i) != null && inventory.getItem(i).getType() == Material.AIR)){

                addButton(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.ORANGE).build(), (player, type) -> {
                });
            }
        }

    }

    private void buildMembers(){

        List<Integer> members = Arrays.asList(10, 11, 12, 13, 14, 15, 16);

        int i = 10;

        for (int a = 0; a < inventory.getSize(); a++) {
            inventory.setItem(a, new ItemStack(Material.AIR));
        }

        for(Map.Entry<String, Boolean> a: team.getMembers().entrySet()){

            String modifier = team.getLeader().equals(a.getKey()) ? "&l" : "";

            ItemStack item ;
                if(team.getLeader().equals(player.getName()) && !a.getKey().equals(player.getName())){
                    item = new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(a.getKey()).setDisplay("&a"+modifier+a.getKey()).
                        setLore("&c", "&aLeft click &7to remove player from the team", "&c").build();
                } else item = new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(a.getKey()).setDisplay("&a"+a.getKey()).build();

            final int slot = i - 10;
            addButton(members.get(slot), item, (player, type) -> {
                if(team.getLeader().equals(player.getName()) && type == ClickType.LEFT){

                    if(a.getKey().equals(player.getName())) return;

                    Player player1 = Bukkit.getPlayer(a.getKey());
                    if(player1 != null){
                        Util.mINFO_noP(player1, UHC.prefix+"You have been removed from the team.");
                        team.getTeamManager().quitTeam(team.getName(), player1);
                    }
                    team.getMembers().remove(a.getKey());
                }

                buttons.remove(members.get(slot));
                buildPage();
            });

            i++;
        }

        addButton(26, new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cBack").build(), (player, type) -> {
            currentPage = TeamMenu.OPTIONS;
            buttons.remove(26);
            buildPage();
        });

        for (int i1 = 0; i1 < members.size(); i1++) {
            if(inventory.getItem(i1 + 10) == null || (inventory.getItem(i1 + 10) != null && inventory.getItem(i1 + 10).getType() == Material.AIR)){
                addButton(i1+10, new ItemBuilder(Material.SKULL_ITEM).build(), (player, type) -> {
                });
            }
        }

        for (int a = 0;a < inventory.getSize(); a++) {
            if(inventory.getItem(a) == null || (inventory.getItem(a) != null && inventory.getItem(a).getType() == Material.AIR)){

                addButton(a, new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.ORANGE).build(), (player, type) -> {
                });
            }
        }


    }

    private void buildInvitePlayers(){

    }

    private enum TeamMenu {


        OPTIONS(new ItemBuilder(Material.GOLD_BLOCK).setDisplay("&cTeam Menu").build(), "Team Options"),
        LEADER(new ItemBuilder(Material.SKULL_ITEM).build(), "Team leader"),
        TEAM_COLOR(new ItemBuilder(Material.WOOL).setDisplay("&7Set your team color").build(), "Set team colour"),
        MEMBERS(new ItemBuilder(Material.DIAMOND_SWORD).setDisplay("&bTeam members").build(), "Team Members"),
        INVITE_PLAYER(new ItemBuilder(Material.IRON_INGOT).setDisplay("&aInvite members").build(), "Invite members"),


        ;


        private final ItemStack itemStack;
        private final String name;

        TeamMenu(ItemStack itemStack, String name){
            this.itemStack = itemStack;
            this.name = name;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public String getName() {
            return name;
        }

    }



}
