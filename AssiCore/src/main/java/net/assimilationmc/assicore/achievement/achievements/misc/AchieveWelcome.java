package net.assimilationmc.assicore.achievement.achievements.misc;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.event.PlayerJoinNetworkEvent;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.Set;

public class AchieveWelcome extends Achievement {

    public AchieveWelcome(AssiPlugin plugin) {
        super(plugin, "WELCOME", AchievementCategory.MISC, "Welcome!", ChatColor.GREEN + "50 bucks",
                "Join AssimilationMC for the first time");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return null;
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(50);
    }

    @EventHandler
    public void on(final PlayerJoinNetworkEvent e) {
        final AssiPlayer player = e.getPlayer();

        if (!player.hasAchievement(getId())) {
            give(player);
        }

    }

}
