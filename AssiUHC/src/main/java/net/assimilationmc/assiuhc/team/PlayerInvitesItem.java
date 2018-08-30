package net.assimilationmc.assiuhc.team;

import net.assimilationmc.assicore.joinitems.JoinItem;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.assiuhc.team.ui.invite.PlayerInvitesMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerInvitesItem extends JoinItem {

    private UHCTeamedGame teamedGame;
    private PlayerInvitesMenu playerInvitesMenu;

    public PlayerInvitesItem(UHCTeamedGame teamedGame) {
        super(2, new ItemBuilder(Material.YELLOW_FLOWER).setDisplay(ChatColor.BLUE + "Invites").build());
        this.teamedGame = teamedGame;
        this.playerInvitesMenu = new PlayerInvitesMenu(teamedGame);
        setGiveCondition(assiPlayer -> {
            Party party = teamedGame.getPlugin().getPartyManager().getPartyOf(assiPlayer.getBase(), false);
            return party == null || party.getLeader().equals(assiPlayer.getUuid());
        });
    }

    @Override
    public void onClick(Player player) {
        if (teamedGame.getTeamManager().hasTeam(player)) {
            player.getInventory().remove(getItemStack());
            return;
        }
        playerInvitesMenu.open(player);
    }

}
