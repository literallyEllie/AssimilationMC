package net.assimilationmc.assiuhc.game.teamed;

import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.UtilAutoLapis;

import java.util.List;
import java.util.UUID;

public class GameDeathmatchTeamed extends UHCTeamedGame {

    public GameDeathmatchTeamed(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("DEATHMATCH_TEAMED", "Teamed Deatchmatch",
                "Play a short game until the death in a small map with boosted resources.", UHCGameSubType.TEAMED_DEATHMATCH.name()));

        getAssiGameSettings().setWarmUpTime((4 * 60) + 30);
        getAssiGameSettings().setMaxGameTime(15 * 60);

        getUHCTeamManager().setMinTeams(2);

        if (getGameMapManager().getSelectedWorld().getName().equals("GravelRoad")) {
            getUHCTeamManager().setMaxTeamSize(3);
            getAssiGameSettings().setMinPlayers(6);
            getAssiGameSettings().setMaxPlayers(6);

        } else {
            getUHCTeamManager().setMaxTeamSize(2);
            getAssiGameSettings().setMinPlayers(4);
            getAssiGameSettings().setMaxPlayers(4);
        }

        // Auto lapis
        plugin.registerListener(new UtilAutoLapis());
    }

    @Override
    public GameTeam electWinner() {
        final List<GameTeam> remainingTeams = getUHCTeamManager().getRemainingTeams();
        return remainingTeams.size() == 1 ? remainingTeams.get(0) : null;
    }

    @Override
    public int winnerXp() {
        return 5;
    }

    @Override
    public int winnerBucks() {
        return 15;
    }

    @Override
    public int winnerUC() {
        return 0;
    }

}
