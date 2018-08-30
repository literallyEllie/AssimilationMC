package net.assimilationmc.ellie.assicore.score.assets;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 9.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiScoreOnline extends AssiScore {

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player) {
        int online = Bukkit.getOnlinePlayers().size();
        if (!player.hasPermission(PermissionLib.STAFF_CHAT)) {
            online = online - AssiCore.getCore().getVanishedPlayers().size();
        }
        ArrayList<String> o = new ArrayList<>();
        o.add(String.valueOf(Util.color(ColorChart.R + online + (online == 1 ? " Player" : " Players"))));
        return o;
    }

}
