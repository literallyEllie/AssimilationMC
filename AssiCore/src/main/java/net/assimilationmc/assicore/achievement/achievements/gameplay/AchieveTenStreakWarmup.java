package net.assimilationmc.assicore.achievement.achievements.gameplay;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.ChatColor;

import java.util.Set;

public class AchieveTenStreakWarmup extends Achievement {

    /**
     * Shell achievement for implementation elsewhere.
     *
     * @param plugin Plugin instance.
     */
    public AchieveTenStreakWarmup(AssiPlugin plugin) {
        super(plugin, "10_WARMUP_STREAK", AchievementCategory.GAME_PLAY, "All warmed up", "100 bucks",
                "Get a 10 kill streak on " + ChatColor.YELLOW + ChatColor.BOLD + "Warmup");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/10 consecutive kills");
    }

    @Override
    public void giveReward(AssiPlayer player) {
    }

}
