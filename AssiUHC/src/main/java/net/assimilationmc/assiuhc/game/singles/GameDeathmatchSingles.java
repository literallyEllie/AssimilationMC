package net.assimilationmc.assiuhc.game.singles;

import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCSinglesGame;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.util.UtilAutoLapis;
import org.bukkit.entity.Player;

import java.util.List;

public class GameDeathmatchSingles extends UHCSinglesGame {

    public GameDeathmatchSingles(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("DEATHMATCH_SINGLES", "Singles Deatchmatch",
                "Play a short game until the death in a small map with boosted resources.", UHCGameSubType.SINGLES_DEATHMATCH.name()));

        getAssiGameSettings().setMaxPlayers(10);

        getAssiGameSettings().setWarmUpTime(4 * 60);
        getAssiGameSettings().setMaxGameTime(15 * 60);


        // Auto lapis
        plugin.registerListener(new UtilAutoLapis());

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
        return 10;
    }

    @Override
    public int winnerUC() {
        return 0;
    }

}
