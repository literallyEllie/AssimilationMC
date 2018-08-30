package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreUTeamCount extends UAssiScore {

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player, UHCGame game) {
        Validate.isTrue(game.getMap().isForTeams(), "ScoreUTeamCount cannot be accessed on an instance of a singles game!");
        int players = game.getPlayers().size();
        ArrayList<String> o = new ArrayList<>();
        int min = game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMinPlayers();
        o.add(String.valueOf(Util.color(UColorChart.VARIABLE)+players + UColorChart.R + "/" +min));
        return o;
    }

}
