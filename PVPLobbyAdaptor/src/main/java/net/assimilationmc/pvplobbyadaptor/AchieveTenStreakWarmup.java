package net.assimilationmc.pvplobbyadaptor;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilMessage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class AchieveTenStreakWarmup extends Achievement {

    public AchieveTenStreakWarmup(PVPLobbyAdaptor lobbyAdaptor) {
        super(lobbyAdaptor.getAssiPlugin(), "10_WARMUP_STREAK", AchievementCategory.GAME_PLAY, "All warmed up", "100 bucks",
                "Get a 10 kill streak on " + ChatColor.YELLOW + ChatColor.BOLD + "Warmup");
    }

    @Override
    public Set<String> showProgress(AssiPlayer assiPlayer) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + assiPlayer.getProgressOrDefault(getId(), "0") + "/10 consecutive kills");
    }

    @Override
    public void giveReward(AssiPlayer assiPlayer) {
        assiPlayer.addBucks(100);
    }

    @Override
    public void showProgressHotbar(AssiPlayer assiPlayer) {
        UtilMessage.sendProgressActionBar(assiPlayer.getBase(), C.V + "Get a kill streak of 10",
                Integer.parseInt(assiPlayer.getProgressOrDefault(getId(), "0")), 10);
    }

    public void trigger(AssiPlayer player) {
        if (player.hasAchievement(getId())) return;
        give(player);
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        getPlugin().getPlayerManager().getOnlinePlayer(e.getPlayer()).clearProgress(getId());
    }

}
