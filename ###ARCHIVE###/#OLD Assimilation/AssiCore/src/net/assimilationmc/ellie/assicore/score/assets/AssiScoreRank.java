package net.assimilationmc.ellie.assicore.score.assets;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.util.Util;
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
        String prefix = group.getPrefix().replace("%space%", "");
        if(prefix.length() == 2){
            prefix = group.getPrefix()+group.getName();
        }

        o.add(group.hasPrefix() ? Util.color(prefix) : group.getName());
        return o;
    }
}
