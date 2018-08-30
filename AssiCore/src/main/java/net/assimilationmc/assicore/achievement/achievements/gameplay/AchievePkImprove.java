package net.assimilationmc.assicore.achievement.achievements.gameplay;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;

import java.util.Set;

public class AchievePkImprove extends Achievement {

    public AchievePkImprove(AssiPlugin plugin) {
        super(plugin, "PK_IMPROVE", AchievementCategory.GAME_PLAY, "Improvement.. I guess", "150 Bucks", "Beat your personal Parkour record");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/1 personal records beat.");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(150);
    }

    public void onBeat(AssiPlayer player) {
        if (player.hasAchievement(getId())) return;
        give(player);
    }

}
