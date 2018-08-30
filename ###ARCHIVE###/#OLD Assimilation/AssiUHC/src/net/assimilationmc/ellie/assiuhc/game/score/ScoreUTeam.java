package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.UHCTeam;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreUTeam extends UAssiScore {

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player, UHCGame game) {
        Validate.isTrue(game.getMap().isForTeams(), "ScoreUTeam cannot be accessed on an instance of a singles game!");
        UHCTeam team = game.getTeamManager().getTeam(player);
        ArrayList<String> o = new ArrayList<>();
        if(team != null){
            o.add(team.getTeamColor().getChatColor() + team.getName());
        }else{
            o.add(Util.color("&7No team"));
        }
        return o;
    }

}
