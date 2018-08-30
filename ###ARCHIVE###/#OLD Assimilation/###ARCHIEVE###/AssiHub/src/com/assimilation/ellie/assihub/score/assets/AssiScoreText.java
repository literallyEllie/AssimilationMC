package com.assimilation.ellie.assihub.score.assets;

import com.assimilation.ellie.assihub.score.ScoreboardManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiScoreText extends AssiScore {

    private String line;

    public AssiScoreText(String line){
        this.line = line;
    }

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player) {
        ArrayList<String> o = new ArrayList<>();
        o.add(line);
        return o;
    }
}
