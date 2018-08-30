package net.assimilationmc.assiuhc.achievement;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assiuhc.event.SinglesWinEvent;
import org.bukkit.event.EventHandler;

import java.util.Set;

public class AchieveSinglesWin extends Achievement {

    public AchieveSinglesWin(AssiPlugin plugin) {
        super (plugin, "SINGLES_WIN", AchievementCategory.GAME_PLAY, "Who needs team-mates?", "5 UC and 25 Bucks", "Win your first singles game");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/1 Singles UHC games won.");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addUltraCoins(5);
        player.addBucks(25);
    }

    @EventHandler
    public void on(SinglesWinEvent e) {
        AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());
        if (player.hasAchievement(getId())) return;
        give(player);
    }

}
