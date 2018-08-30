package net.assimilationmc.assiuhc.game.teamed;

import net.assimilationmc.assiuhc.border.GameBorder;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.UtilAutoLapis;

import java.util.List;

public class GameClassicTeamed extends UHCTeamedGame {

    public GameClassicTeamed(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("CLASSIC_TEAMED", "Teamed Classic",
                "The original UHC that we all know and love with teams!", UHCGameSubType.TEAMED_CLASSIC.name()));

        getAssiGameSettings().setWarmUpTime(10 * 60);
        getAssiGameSettings().setMaxGameTime(45 * 60);

        getAssiGameSettings().setMinPlayers(2);
        getAssiGameSettings().setMaxPlayers(100);

        getUHCTeamManager().setMaxTeamSize(4);

        // Auto lapis
        plugin.registerListener(new UtilAutoLapis());
        setGameBorder(new GameBorder(this));
    }

    @Override
    public GameTeam electWinner() {
        final List<GameTeam> remainingTeams = getUHCTeamManager().getRemainingTeams();
        return remainingTeams.size() == 1 ? remainingTeams.get(0) : null;
    }

    @Override
    public int winnerXp() {
        return 7;
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
