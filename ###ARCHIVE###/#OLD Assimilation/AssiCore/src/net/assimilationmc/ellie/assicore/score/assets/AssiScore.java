package net.assimilationmc.ellie.assicore.score.assets;

import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public abstract class AssiScore {

    public abstract ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player);

}
