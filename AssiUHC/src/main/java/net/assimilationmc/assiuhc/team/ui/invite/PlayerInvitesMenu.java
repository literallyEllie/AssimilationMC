package net.assimilationmc.assiuhc.team.ui.invite;

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

import java.util.Set;

public class PlayerInvitesMenu extends UI {

    private UHCTeamedGame teamedGame;

    public PlayerInvitesMenu(UHCTeamedGame uhcTeamedGame) {
        super(uhcTeamedGame.getPlugin(), C.C + "Team invites", 27);
        this.teamedGame = uhcTeamedGame;
    }

    @Override
    public void open(Player player) {
        removeAllButtons();
        final Set<String> invitesOf = teamedGame.getUHCTeamManager().getInvitesOf(player);

        for (String teamName : invitesOf) {
            GameTeam team = teamedGame.getTeamManager().getTeam(teamName);
            if (team == null) continue;
            Player leader = teamedGame.getUHCTeamManager().getTeamLeader(team);

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(leader.getName()).setDisplay(team.getColor() + leader.getName() + GC.C + "'s team")
                            .setLore(C.C, GC.C + "An invite from " + team.getColor() + teamName, C.C,
                                    ChatColor.GREEN + "Left-click to accept.", ChatColor.RED + "Right-click to decline.").build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                    if (clickType.isRightClick() && invitesOf.contains(team.getName())) {
                        player.sendMessage(GC.C + "Declined the request to join the team " + GC.V + teamName + GC.II + ".");
                        teamedGame.getUHCTeamManager().declineInvite(player, team);
                    }

                    if (clickType.isLeftClick() && teamedGame.getUHCTeamManager().joinTeam(player, team)) {
                        closeInventory(player);
                    }

                }
            });
        }

        super.open(player);
    }

}
