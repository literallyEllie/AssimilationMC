package net.assimilationmc.assiuhc.game.singles;

import net.assimilationmc.assiuhc.border.GameBorder;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCSinglesGame;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import org.bukkit.entity.Player;

import java.util.List;

public class GameSkyUHCSingles extends UHCSinglesGame {

    public GameSkyUHCSingles (GamePlugin plugin) {
        super (plugin, new AssiGameMeta("SKY_SINGLES", "SkyUHC Singles", "UHC in the sky!", UHCGameSubType.SINGLES_SKY.name()));

        getDropManager().unregisterAllPackages();
        setGameBorder(new GameBorder(this));
        getGameBorder().setInitSize(150);

        // getAssiGameSettings().setWarmUpTime();
    }

    @Override
    public Player electWinner() {
        List<Player> livePlayers = getLivePlayers();
        return livePlayers.size() == 1 ? livePlayers.get(0) : null;
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
