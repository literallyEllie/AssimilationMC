package net.assimilationmc.assiuhc.team.ui;

import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.util.GC;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeamCreateMenu implements Listener {

    private UHCTeamedGame game;
    private Player player;
    private Inventory inventory;
    private String proposedTeamName;

    public TeamCreateMenu(UHCTeamedGame game, Player player) {
        this.game = game;
        this.player = player;
        game.getPlugin().registerListener(this);
        build();
    }

    @EventHandler
    public void on(final InventoryCloseEvent e) {
        if (e.getPlayer() == player) {
            inventory.setItem(0, null);
            inventory.setItem(2, null);
            unregister();
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent e) {
        if (player != e.getWhoClicked()) return;
        e.setCancelled(true);

//        e.setCurrentItem(null);

        if (e.getRawSlot() == 2) {
            final ItemStack clicked = inventory.getItem(e.getRawSlot());
            if (clicked == null || clicked.getType() == Material.AIR) return;

            proposedTeamName = clicked.getItemMeta().getDisplayName();

            if (proposedTeamName.length() > 1) {
                if (proposedTeamName.length() < 3 || proposedTeamName.length() > 7) {
                    inventory.setItem(2, new ItemBuilder(Material.DIAMOND_HELMET).setDisplay("")
                            .setLore(GC.II + "Name too short/long.").build());
                    return;
                }

                if (game.getTeamManager().isTeam(proposedTeamName)) {
                    inventory.setItem(2, new ItemBuilder(Material.DIAMOND_HELMET).setDisplay("")
                            .setLore(GC.II + "Team name already taken.").build());
                    return;
                }

                player.sendMessage(game.getUHCTeamManager().createTeam(player, proposedTeamName));
                player.closeInventory();
            }

        }

//        if (e.getRawSlot() < 3) {
//            if (e.getRawSlot() == 2 && proposedTeamName.length() > 1) {
//                if (proposedTeamName.length() < 3 || proposedTeamName.length() > 7) {
//                    inventory.setItem(2, new ItemBuilder(Material.DIAMOND_HELMET).setDisplay("")
//                            .setLore(GC.II + "Name too short/long.").build());
//                    return;
//                }
//
//                if (game.getTeamManager().isTeam(proposedTeamName)) {
//                    inventory.setItem(2, new ItemBuilder(Material.DIAMOND_HELMET).setDisplay("")
//                            .setLore(GC.II + "Team name already taken.").build());
//                    return;
//                }
//
//                player.sendMessage(game.getUHCTeamManager().createTeam(player, proposedTeamName));
//                player.closeInventory();
//            }
//
//        }
    }

    private void unregister() {
        HandlerList.unregisterAll(this);
    }

    private void build() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        AnvilContainer container = new AnvilContainer(entityPlayer);
        int c = entityPlayer.nextContainerCounter();

        Packet packet = new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name"), 0);
        entityPlayer.playerConnection.sendPacket(packet);

        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.windowId = c;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);

        inventory = container.getBukkitView().getTopInventory();
        inventory.setItem(0, new ItemBuilder(Material.DIAMOND_HELMET).setDisplay("").setLore(ChatColor.GREEN + "Type in name slot the team name" +
                " you desire.", GC.II + "It must be between 3 and 7 characters").build());


    }

    private class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(EntityHuman entityHuman) {
            super(entityHuman.inventory, entityHuman.world, new BlockPosition(0, 0, 0), entityHuman);
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public void a(String s) {
//            proposedTeamName = s.trim().replace(" ", "");
//
//            if (getSlot(2).hasItem()) {
//                net.minecraft.server.v1_8_R3.ItemStack itemStack = getSlot(2).getItem();
//
//                if (StringUtils.isEmpty(s)) {
//                    itemStack.r();
//                } else itemStack.c(proposedTeamName);
//            }

            super.a(s);
        }

    }

}
