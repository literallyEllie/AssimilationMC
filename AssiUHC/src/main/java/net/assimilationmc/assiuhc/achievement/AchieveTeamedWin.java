package net.assimilationmc.assiuhc.achievement;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assiuhc.event.TeamedPlayerWinEvent;
import org.bukkit.event.EventHandler;

import java.util.Set;

public class AchieveTeamedWin extends Achievement {

    public AchieveTeamedWin(AssiPlugin plugin) {
        super(plugin, "TEAMED_WIN", AchievementCategory.GAME_PLAY, "Team player", "5 UC and 15 Bucks", "Win your first teamed game");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/1 Teamed UHC games won.");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addUltraCoins(5);
        player.addBucks(25);
    }

    @EventHandler
    public void on(TeamedPlayerWinEvent e) {
        if (e.getAssiPlayer().hasAchievement(getId())) return;
        give(e.getAssiPlayer());
    }

}