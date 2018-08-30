package net.assimilationmc.assicore.achievement.achievements.misc;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Month;
import java.util.Set;

public class AchieveJoinXmas extends Achievement {

    public AchieveJoinXmas(AssiPlugin plugin) {
        super(plugin, "JOIN_XMAS", AchievementCategory.MISC, "Join on Christmas day!", ChatColor.GREEN + "200 bucks",
                "Join on Christmas Day for a ", "special gift from us at", "AssimilationMC <3");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(200);
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "Join on 25th December");

    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (UtilTime.isMonth(Month.DECEMBER) && UtilTime.isDay(25)) {
            give(player);
        }
    }

}