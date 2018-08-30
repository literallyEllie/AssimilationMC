package net.assimilationmc.ellie.assicore.score.assets;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiScoreCoins extends AssiScore {

    @Override
    public ArrayList<String> getLines(ScoreboardManager scoreboardManager, Player player) {
        int coins = ModuleManager.getModuleManager().getEconomyManager().getEconomy().getBalance(player.getUniqueId());

        ArrayList<String> o = new ArrayList<>();
        o.add(String.valueOf(Util.color("&7")+coins));
        return o;
    }
}
