package net.assimilationmc.assiuhc.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.UHCSinglesGame;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdForceRespawn extends AssiCommand {

    private final UHCGame game;

    public CmdForceRespawn(UHCGame game) {
        super(game.getPlugin(), "forcerespawn", "Bring a player back from the spectator team", Rank.ADMIN, Lists.newArrayList(), "<player>");
        this.game = game;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = UtilPlayer.get(args[0]);

        if (player == null) {
            couldNotFind(commandSender, args[0]);
            return;
        }

        GameTeam team = game.getTeamManager().getTeam(player);
        if (team != null && !team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
            commandSender.sendMessage(C.II + "Player is not on spectator team.");
            return;
        }

        team = game.getDeathLogger().getOldTeam(player);
        if (team == null && game instanceof UHCTeamedGame) {
            commandSender.sendMessage(C.II + "Player never had a team.");
            return;
        }

        if (game instanceof UHCSinglesGame) {
            team = game.getTeamManager().getDefaultTeam();
        }


        if (team != null && !game.getTeamManager().isTeam(team.getName())) {
            commandSender.sendMessage(C.II + "Team no long exist");
            return;
        }

        game.getTeamManager().removeFromAnyTeam(player);
        game.getSpectateManager().unset(player);

        if (game instanceof UHCTeamedGame) {
            ((UHCTeamedGame) game).getUHCTeamManager().joinTeam(player, team);
        } else {
            team.add(player);
        }

        player.sendMessage(C.II + "You have been respawned by " + commandSender.getName());
        commandSender.sendMessage(C.II + "Respawned. You should probably teleport them or something.");

    }

}
