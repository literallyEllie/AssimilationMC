package net.assimilationmc.assiuhc.team.ui;

import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TeamMembersMenu extends UI {

    private UHCTeamedGame game;
    private GameTeam team;

    public TeamMembersMenu(UHCTeamedGame game, GameTeam team) {
        super(game.getPlugin(), GC.C + "Team Members", 27);
        this.game = game;
        this.team = team;
    }

    @Override
    public void open(Player player) {
        refresh(player);
        super.open(player);
    }

    private void refresh(Player player) {
        removeAllButtons();
        Player teamLeader = game.getUHCTeamManager().getTeamLeader(team);
        boolean leader = game.getUHCTeamManager().isTeamLeader(player);

        addButton(new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(teamLeader.getName()).setDisplay(C.II + teamLeader.getName()).setLore(C.C, GC.C + "Team leader").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
            }
        });

        Party leaderParty = getPlugin().getPartyManager().getPartyOf(player, false);

        for (UUID uuid : team.getPlayers()) {
            Player member = UtilPlayer.get(uuid);
            if (member == teamLeader) continue;

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    final ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM);

                    if (member == null) {
                        itemBuilder.setDisplay(GC.C + "Null player").setLore(GC.C + ChatColor.ITALIC + "Player possibly offline");
                    } else itemBuilder.asPlayerHead(member.getName()).setDisplay(GC.V + member.getName());

                    if (leader && (member != null && leaderParty != null && !leaderParty.getMembers().contains(member.getUniqueId()))) {
                        itemBuilder.setLore(C.C, GC.II + "Right-click to remove player from team.");
                    }

                    return itemBuilder.build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                    if (!(leader && clickType.isRightClick())) return;
                    if (leaderParty != null && leaderParty.getMembers().contains(member.getUniqueId())) return;

                    game.getUHCTeamManager().handleKick(team, member);
                    team.message(GC.V + ChatColor.BOLD + member.getName() + GC.II + ChatColor.BOLD + " was removed from the team by "
                            + GC.V + ChatColor.BOLD + assiPlayer.getName() + GC.II + ChatColor.BOLD + ".");

                }
            });

        }

        addButton(26, new Button() {
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
