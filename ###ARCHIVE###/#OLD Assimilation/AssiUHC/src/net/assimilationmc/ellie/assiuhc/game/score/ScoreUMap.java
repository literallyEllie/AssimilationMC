package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreUMap extends UAssiScore {

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player, UHCGame game) {
        String map = game.getMap().getName();
        ArrayList<String> o = new ArrayList<>();
        o.add(String.valueOf(Util.color(ColorChart.R)+map));
        return o;
    }

}
