package net.assimilationmc.assicore.achievement;

import net.assimilationmc.assicore.event.AssiEvent;
import net.assimilationmc.assicore.player.AssiPlayer;

public class AchievementAchieveEvent extends AssiEvent {

    private final Achievement achievement;
    private final AssiPlayer player;

    /**
     * Called when a player achieves an achievement.
     *
     * @param achievement Achievement they achieved
     * @param player      Player whom achieved it.
     */
    public AchievementAchieveEvent(Achievement achievement, AssiPlayer player) {
        this.achievement = achievement;
        this.player = player;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public AssiPlayer getPlayer() {
        return player;
    }

}
