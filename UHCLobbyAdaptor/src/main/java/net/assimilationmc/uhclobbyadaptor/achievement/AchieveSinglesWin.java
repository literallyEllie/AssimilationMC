package net.assimilationmc.uhclobbyadaptor.achievement;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;

import java.util.Set;

public class AchieveSinglesWin extends Achievement {

    // Skeleton
    public AchieveSinglesWin(AssiPlugin plugin) {
        super (plugin, "SINGLES_WIN", AchievementCategory.GAME_PLAY, "Who needs team-mates?", "5 UC and 25 Bucks", "Win your first singles game");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/1 Singles UHC games won.");
    }

    @Override
    public void giveReward(AssiPlayer player) {
    }

}
