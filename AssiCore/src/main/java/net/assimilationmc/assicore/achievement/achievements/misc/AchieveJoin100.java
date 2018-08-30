package net.assimilationmc.assicore.achievement.achievements.misc;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class AchieveJoin100 extends Achievement {

    public AchieveJoin100(AssiPlugin plugin) {
        super(plugin, "JOIN_100", AchievementCategory.MISC, "Join 100 times", ChatColor.GREEN + "100 bucks",
                "Show your true commitment ", "and join the server 100 times! ", "True hero.");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(100);
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + player.getJoins() + "/100 joins");
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (player.getJoins() >= 100 && !player.getAchievements().containsKey(getId())) {
            give(player);
        }
    }

}