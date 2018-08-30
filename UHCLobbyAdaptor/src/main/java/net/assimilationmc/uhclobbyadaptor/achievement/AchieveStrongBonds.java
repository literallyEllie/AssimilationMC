package net.assimilationmc.uhclobbyadaptor.achievement;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import net.assimilationmc.uhclobbyadaptor.stats.UHCPlayer;

import java.util.Set;

public class AchieveStrongBonds extends Achievement {

    private final UHCLobbyAdaptor uhcLobbyAdaptor;

    public AchieveStrongBonds(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super (uhcLobbyAdaptor.getAssiPlugin(), "STRONG_BONDS", AchievementCategory.GAME_PLAY, "Win 5 games of Scatter", "10 UC and 100 Bucks",
                "Win 5 Scatter games");
        this.uhcLobbyAdaptor = uhcLobbyAdaptor;
    }

    @Override
    public Set<String> showProgress(AssiPlayer assiPlayer) {
        UHCPlayer player = uhcLobbyAdaptor.getROuhcStatsProvider().getPlayer(assiPlayer.getBase());

        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + player.getPreviousGamesPlayed().
                getOrDefault(UHCGameSubType.TEAMED_SCATTER, 0) + "/5 Scatter games won.");
    }

    @Override
    public void giveReward(AssiPlayer assiPlayer) {
    }

}
