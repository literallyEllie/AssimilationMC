package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.score.assets.AssiScore;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public abstract class UAssiScore extends AssiScore {

    public abstract ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player, UHCGame game);

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player) {
        return getLines(scoreboardManager, player, UHC.getPlugin(UHC.class).getGameManager().getPlayerGame(player)); // might throw null??
    }

}
