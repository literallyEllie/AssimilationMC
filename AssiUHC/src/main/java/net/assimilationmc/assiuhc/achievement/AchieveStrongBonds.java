package net.assimilationmc.assiuhc.achievement;

import com.google.common.collect.Sets;

import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.player.UHCPlayer;

import java.util.Set;

public class AchieveStrongBonds extends Achievement {

    private final UHCGame game;

    public AchieveStrongBonds(UHCGame game) {
        super (game.getPlugin(), "STRONG_BONDS", AchievementCategory.GAME_PLAY, "Win 5 games of Scatter", "10 UC and 100 Bucks", "Win 5 Scatter games");
        this.game = game;
    }

    @Override
    public Set<String> showProgress(AssiPlayer assiPlayer) {
        UHCPlayer player = game.getPlayerManager().getPlayer(assiPlayer.getUuid());

        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + player.getPreviousGamesPlayed().
                getOrDefault(UHCGameSubType.TEAMED_SCATTER, 0) + "/5 Scatter games won.");
    }

    @Override
    public void giveReward(AssiPlayer assiPlayer) {
        assiPlayer.addUltraCoins(10);
        assiPlayer.addBucks(100);
    }

}
