package net.assimilationmc.assiuhc.game.singles;

import net.assimilationmc.assiuhc.border.GameBorder;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCSinglesGame;
import net.assimilationmc.assiuhc.game.custom.CustomizationProperties;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import org.bukkit.entity.Player;

import java.util.List;

public class GameClassicSingles extends UHCSinglesGame {

    public GameClassicSingles(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("SINGLES_CLASSIC", "Classic Singles",
                "The original UHC we all know and love.", UHCGameSubType.SINGLES_CLASSIC.name()));

        getAssiGameSettings().setMinPlayers(3);
        getAssiGameSettings().setMaxPlayers(100);

        getAssiGameSettings().setWarmUpTime(10 * 60);
        getAssiGameSettings().setMaxGameTime(45 * 60);

        setGameBorder(new GameBorder(this));

    }

    @Override
    public Player electWinner() {
        List<Player> livePlayers = getLivePlayers();
        return livePlayers.size() == 1 ? livePlayers.get(0) : null;
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
