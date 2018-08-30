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

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AchieveDay30SinceJoin extends Achievement {

    public AchieveDay30SinceJoin(AssiPlugin plugin) {
        super(plugin, "FIRST_JOIN_30D", AchievementCategory.MISC, "30 day anniversary!", ChatColor.GREEN + "150 bucks",
                "Join again 30 days ", "after, first joining! ", "<3");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(150);
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "You joined " + UtilTime.formatTimeStamp(player.getFirstSeen()));
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (UtilTime.elapsed(player.getFirstSeen(), TimeUnit.DAYS.toMillis(30))
                && !player.getAchievements().containsKey(getId())) {
            give(player);
        }
    }

}
