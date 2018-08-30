package com.assimilation.ellie.assihub.score.assets;

import com.assimilation.ellie.assihub.score.ScoreboardManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public abstract class AssiScore {

    public abstract ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player);

}
