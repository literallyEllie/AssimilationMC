package net.assimilationmc.assiuhc.game.singles.op;

import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.UHCSinglesGame;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import org.bukkit.entity.Player;

public class GameSinglesOP extends UHCSinglesGame {

    public GameSinglesOP(GamePlugin plugin) {
        super(plugin, new AssiGameMeta("SINGLES_OP", "OP UHC", "UHC but you think you're getting good stuff easily", UHCGameSubType.SINGLES_OP.name()));

        // Drop packages
        getDropManager().unregisterAllPackages();
        getDropManager().registerDropPackage(new OPDropPackage(((UHCGame) plugin.getAssiGame())));


    }

    @Override
    public void onGameEnd() {
        super.onGameEnd();
    }

    @Override
    public Player electWinner() {
        return null;
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
