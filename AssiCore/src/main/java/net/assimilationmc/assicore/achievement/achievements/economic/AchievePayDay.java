package net.assimilationmc.assicore.achievement.achievements.economic;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;

import java.util.Set;

public class AchievePayDay extends Achievement {

    public AchievePayDay(AssiPlugin plugin) {
        super(plugin, "PAY_DAY", AchievementCategory.ECONOMIC, "Pay Day", "100 bucks",
                "Get paid at least 100 bucks by another player");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/1 payments received");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(100);
    }

    public void onPayment(AssiPlayer player, int amount) {
        if (player.hasAchievement(getId())) return;
        if (amount < 100) {
            return;
        }

        give(player);
    }

}
