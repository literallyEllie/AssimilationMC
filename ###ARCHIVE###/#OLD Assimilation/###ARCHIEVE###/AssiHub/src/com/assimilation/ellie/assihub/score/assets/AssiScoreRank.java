package com.assimilation.ellie.assihub.score.assets;

import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.permission.AssiPermGroup;
import com.assimilation.ellie.assicore.util.Util;
import com.assimilation.ellie.assihub.score.ScoreboardManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiScoreRank extends AssiScore {

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player) {
        AssiPermGroup group = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(player.getUniqueId());

        ArrayList<String> o = new ArrayList<>();
        o.add(group.hasPrefix() ? Util.color(group.getPrefix()) : group.getName());
        return o;
    }
}
