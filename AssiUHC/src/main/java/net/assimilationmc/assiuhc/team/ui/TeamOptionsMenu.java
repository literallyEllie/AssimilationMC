package net.assimilationmc.assiuhc.team.ui;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TeamOptionsMenu extends UI {

    private UHCTeamedGame game;
    private String leader;
    private GameTeam team;
    private int maxTeamSize;

    // Sub Menus
    private TeamColorsMenu teamColorsMenu;
    private TeamMembersMenu teamMembersMenu;
    private TeamInviteMenu teamInviteMenu;


    public TeamOptionsMenu(UHCTeamedGame uhcGame, String leader, GameTeam team, int maxTeamSize) {
        super(uhcGame.getPlugin(), team.getColor() + team.getName(), 9);
        this.game = uhcGame;
        this.leader = leader;
        this.team = team;
        this.maxTeamSize = maxTeamSize;

        this.teamColorsMenu = new TeamColorsMenu(uhcGame, team);
        this.teamMembersMenu = new TeamMembersMenu(uhcGame, team);
        this.teamInviteMenu = new TeamInviteMenu(uhcGame, team);
    }

    @Override
    public void open(Player player) {
        buildMainPage();
        super.open(player);
    }

    @Override
    public void destroySelf() {
        // destroy other menus as well
        teamColorsMenu.destroySelf();
        teamMembersMenu.destroySelf();
        teamInviteMenu.destroySelf();

        super.destroySelf();
    }

    private void buildMainPage() {
        removeAllButtons();

        addButton(0, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.GOLD_BLOCK).setDisplay(GC.C + "Team Options Menu")
                        .setLore(C.C, GC.C + "Team Size: " + GC.V + team.getPlayers().size(),
                                GC.C + "Max Team Size: " + GC.V + maxTeamSize).build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
            }
        });

        addButton(1, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(leader).setDisplay(C.V + leader)
                        .setLore(C.C, GC.C + "The team leader.").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
            }
        });

        addButton(2, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.WOOL).setColor(ItemBuilder.StackColor.fromChatColor(team.getColor()))
                        .setDisplay(GC.C + "The Team color").setLore(C.C, (assiPlayer.getName().equals(leader) ? GC.II + "Click to change." : C.C)).build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                if (!assiPlayer.getName().equals(leader)) return;
                buildTeamColors(assiPlayer.getBase());
            }
        });

        addButton(3, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.DIAMOND_SWORD).setDisplay(GC.C + "Team Members").
                        setLore(C.C, GC.C + "View all the team members in the team.").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                buildTeamMembers(assiPlayer.getBase());
            }
        });

        addButton(4, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.IRON_INGOT).setDisplay(C.C + "Invite members")
                        .setLore(C.C, (assiPlayer.getName().equals(leader) ? GC.C + "Click to invite members." : GC.II + "Only the team leader " +
                                "can invite members.")).build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                if (!assiPlayer.getName().equals(leader)) return;
                buildInviteMembers(assiPlayer.getBase());
            }
        });

        addButton(5, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.REDSTONE_BLOCK).setDisplay(GC.II + "Leave the team").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                closeInventory(assiPlayer.getBase());
                game.getUHCTeamManager().leaveTeam(assiPlayer.getBase());
            }
        });


        for (int i = 5; i < getInventorySize() - 1; i++) {
            if (isButton(i)) continue;
            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return getFiller();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                }
            });
        }

        addButton(8, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(GC.II + "Close Menu").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                closeInventory(assiPlayer.getBase());
            }
        });

    }

    private void buildTeamColors(Player player) {
        closeInventory(player);
        teamColorsMenu.open(player);
    }

    private void buildTeamMembers(Player player) {
        closeInventory(player);
        teamMembersMenu.open(player);
    }

    private void buildInviteMembers(Player player) {
        closeInventory(player);
        teamInviteMenu.open(player);
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

}
