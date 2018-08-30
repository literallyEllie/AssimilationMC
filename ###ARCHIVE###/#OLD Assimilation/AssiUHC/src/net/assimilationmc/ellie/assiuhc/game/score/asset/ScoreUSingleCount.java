package net.assimilationmc.ellie.assiuhc.game.score.asset;

import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.score.UAssiScore;
import net.assimilationmc.ellie.assiuhc.game.score.UHCScoreboardManager;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreUSingleCount extends UAssiScore {

    @Override
    public ArrayList<String> getLines(UHCScoreboardManager scoreboardManager, Player player, UHCGame game) {
        Validate.isTrue(!game.getMap().isForTeams(), "ScoreUSingleCount cannot be accessed on an instance of a teams game!");
        int players = game.getPlayers().size();
        ArrayList<String> o = new ArrayList<>();
        int min = game.getMap().getSingledGameType().get(game.getMap().getSelectedSingled()).getMinPlayers();
        o.add(String.valueOf(Util.color(UColorChart.VARIABLE+players + UColorChart.R + "/" +min)));
        return o;
    }

}
