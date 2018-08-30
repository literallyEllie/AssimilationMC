package net.assimilationmc.ellie.assiuhc.game.score.asset;

import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.score.UAssiScore;
import net.assimilationmc.ellie.assiuhc.game.score.UHCScoreboardManager;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreUPlayerCount extends UAssiScore {

    @Override
    public ArrayList<String> getLines(UHCScoreboardManager scoreboardManager, Player player, UHCGame game) {
        int players = game.getPlayers().size();
        ArrayList<String> o = new ArrayList<>();
        o.add(String.valueOf(Util.color(UColorChart.VARIABLE)+players));
        return o;
    }

}
