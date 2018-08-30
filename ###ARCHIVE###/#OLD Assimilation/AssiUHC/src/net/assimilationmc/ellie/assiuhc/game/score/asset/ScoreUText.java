package net.assimilationmc.ellie.assiuhc.game.score.asset;

import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.score.UAssiScore;
import net.assimilationmc.ellie.assiuhc.game.score.UHCScoreboardManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 16.7.17 for votifier.
 * Affiliated with www.minevelop.com
 */
public class ScoreUText extends UAssiScore {

    private String text;

    public ScoreUText(String text){
        this.text = text;
    }

    @Override
    public ArrayList<String> getLines(UHCScoreboardManager scoreboardManager, Player player, UHCGame game) {
        ArrayList<String> a = new ArrayList<>();
        a.add(Util.color(text));
        return a;
    }
}
