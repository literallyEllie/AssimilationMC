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
public class ScoreUMap extends UAssiScore {

    @Override
    public ArrayList<String> getLines(UHCScoreboardManager scoreboardManager, Player player, UHCGame game) {
        String map = game.getMap().getName();
        ArrayList<String> o = new ArrayList<>();
        o.add(String.valueOf(Util.color(UColorChart.VARIABLE)+map));
        return o;
    }

}
