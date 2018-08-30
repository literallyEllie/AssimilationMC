package net.assimilationmc.assiuhc.team.ui;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TeamInviteMenu extends UI {

    private UHCTeamedGame game;
    private GameTeam team;

    public TeamInviteMenu(UHCTeamedGame game, GameTeam team) {
        super(game.getPlugin(), C.C + "Invite players", 54);
        this.game = game;
        this.team = team;
    }

    @Override
    public void open(Player player) {
        refresh();
        super.open(player);
    }

    private void refresh() {
        removeAllButtons();

        if (game.getUHCTeamManager().getMaxTeamSize() <= team.getPlayers().size()) {
            addButton(22, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return new ItemBuilder(Material.REDSTONE_BLOCK).setDisplay(GC.II + "Your team is full!").build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                }
            });
            return;
        }

        for (Player oPlayer : game.getTeamManager().getTeamless()) {
            boolean invited = game.getUHCTeamManager().hasInvited(team, oPlayer);

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(oPlayer.getName()).setDisplay(C.V + oPlayer.getName())
                            .setLore(C.C, (invited ? GC.II + "Right-click to withdraw invite." : GC.C + "Left-click to invite."),
                                    GC.II + ChatColor.BOLD + "Spamming invites will result in punishment.").build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                    if (clickType.isRightClick() && invited) {
                        game.getUHCTeamManager().unsendInviteTo(oPlayer, assiPlayer.getBase(), team);
                        open(assiPlayer.getBase());
                    } else if (clickType.isLeftClick() && !invited) {
                        game.getUHCTeamManager().sendInviteTo(oPlayer, assiPlayer.getBase(), team);
                        open(assiPlayer.getBase());
                    }
                }
            });
        }

        addButton(53, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(GC.II + "Back").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                closeInventory(assiPlayer.getBase());
                game.getUHCTeamManager().openTeamUI(assiPlayer.getBase());
            }
        });

    }

}
