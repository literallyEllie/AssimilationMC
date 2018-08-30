package net.assimilationmc.assiuhc.achievement;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.gameapi.spectate.PlayerGameDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Set;

public class AchieveFirstBlood extends Achievement {

    public AchieveFirstBlood(AssiPlugin plugin) {
        super (plugin, "FIRST_BLOOD", AchievementCategory.GAME_PLAY, "First Blood", "30 Bucks", "Be the first one to kill a player in a game");
    }

    @Override
    public Set<String> showProgress(AssiPlayer assiPlayer) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + "0/1 First Bloods");
    }

    @Override
    public void giveReward(AssiPlayer assiPlayer) {
        assiPlayer.addBucks(30);
    }

    @EventHandler
    public void on(final PlayerGameDeathEvent e) {
        if (!(e.getKiller() instanceof Player)) return;

        AssiPlayer player = getPlugin().getPlayerManager().getOnlinePlayer((Player) e.getKiller());
        if (player.hasAchievement(getId())) return;
        give(player);
    }

}
