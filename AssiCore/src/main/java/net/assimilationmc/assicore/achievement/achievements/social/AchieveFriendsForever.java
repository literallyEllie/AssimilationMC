package net.assimilationmc.assicore.achievement.achievements.social;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.event.PlayerJoinNetworkEvent;
import net.assimilationmc.assicore.friend.FriendMakeEvent;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.Set;

public class AchieveFriendsForever extends Achievement {

    public AchieveFriendsForever(AssiPlugin plugin) {
        super(plugin, "FIRST_FRIEND", AchievementCategory.SOCIAL, "Friends forever!", ChatColor.GREEN + "50 bucks",
                "Make one friend with /fr");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return Sets.newHashSet(AchievementProgressList.Status.TODO.getPrefix() + player.getFriendData().getFriends().size() + "/1 friends");
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(50);
    }

    @EventHandler
    public void on(final PlayerJoinNetworkEvent e) {
        checkGive(e.getPlayer());
    }

    @EventHandler
    public void on(final FriendMakeEvent e) {
        checkGive(e.getSender());
        checkGive(e.getAcceptor());
    }

    private void checkGive(AssiPlayer player) {
        if (player == null || !player.isOnline() || player.hasAchievement(getId())) return;

        if (player.getFriendData().getFriends().size() >= 1)
            give(player);
    }

}
