package net.assimilationmc.assicore.achievement.achievements.secret;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Set;

public class AchievePunchOwner extends Achievement {

    public AchievePunchOwner(AssiPlugin plugin) {
        super(plugin, "PUNCH_OWNER", AchievementCategory.SECRET, "Punch xEline", C.UC + "50 UC", true,
                "OOF");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        return null;
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addUltraCoins(50);
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player
                && e.getEntity() instanceof Player) {

            if (!e.getEntity().getName().equals("xEline")) return;

            AssiPlayer player = getPlugin().getPlayerManager().getPlayer(((Player) e.getDamager()));
            if (player.hasAchievement(getId())) return;

            give(player);

        }

    }

}
