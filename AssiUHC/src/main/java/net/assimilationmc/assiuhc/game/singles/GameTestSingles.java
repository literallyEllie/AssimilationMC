package net.assimilationmc.assiuhc.game.singles;

import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCSinglesGame;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import org.bukkit.entity.Player;

public class GameTestSingles extends UHCSinglesGame {

    public GameTestSingles(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("TEST_SINGLES", "Test S UHC Game", "Test UHC game", UHCGameSubType.TEST_SINGLES.name()));

        getAssiGameSettings().setWarmUpTime(10);
        getAssiGameSettings().setMaxGameTime(2700);
    }

    @Override
    public Player electWinner() {
        return null;
//        List<Player> livePlayers = getLivePlayers();
//        return livePlayers.size() == 1 ? livePlayers.get(0) : null;
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

}
