package net.assimilationmc.assiuhc.game.teamed;

import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.team.GameTeam;

public class GameTestTeamed extends UHCTeamedGame {

    public GameTestTeamed(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("TEST_TEAMED", "Test UHC Game", "Test UHC game", UHCGameSubType.TEST_TEAMED.name()));

        getAssiGameSettings().setLobbyTime(300);
        getAssiGameSettings().setWarmUpTime(10);
    }

    @Override
    public int winnerXp() {
        return 0;
    }

    @Override
    public int winnerBucks() {
        return 0;
    }

    @Override
    public int winnerUC() {
        return 0;
    }

    @Override
    public GameTeam electWinner() {
        return null;
    }

}
