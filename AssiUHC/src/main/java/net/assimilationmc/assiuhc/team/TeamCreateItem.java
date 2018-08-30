package net.assimilationmc.assiuhc.team;

import net.assimilationmc.assicore.joinitems.JoinItem;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.assiuhc.team.ui.TeamCreateMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeamCreateItem extends JoinItem {

    private UHCTeamedGame game;

    public TeamCreateItem(UHCTeamedGame uhcGame) {
        super(1, new ItemBuilder(Material.NAME_TAG).setDisplay(ChatColor.GREEN + "Teams")
                .setLore(ChatColor.RED + "Left click this to open the team menu.").build());
        this.game = uhcGame;
        setGiveCondition(assiPlayer -> {
            Party party = uhcGame.getPlugin().getPartyManager().getPartyOf(assiPlayer.getBase(), false);
            return party == null || party.getLeader().equals(assiPlayer.getUuid());
        });
    }

    @Override
    public void onClick(Player player) {
        if (game.getTeamManager().hasTeam(player)) {
            game.getUHCTeamManager().openTeamUI(player);
            return;
        }
        if (!getGiveCondition().onJoin(game.getPlugin().getPlayerManager().getPlayer(player))) {
            player.getInventory().remove(getItemStack());
            player.sendMessage(C.C + "You have joined a party.");
            return;
        }

        if (game.getTeamManager().getTeams().size() > game.getUHCTeamManager().getMaxTeams()) {
            player.sendMessage(C.C + "The current max team amount has been reached.");
            return;
        }

        player.closeInventory();
        new TeamCreateMenu(game, player);
    }

}
