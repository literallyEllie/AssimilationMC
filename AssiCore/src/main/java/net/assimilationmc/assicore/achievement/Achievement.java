package net.assimilationmc.assicore.achievement;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;

public abstract class Achievement implements Listener {

    private final AssiPlugin plugin;
    private final String id, display, reward;
    private final AchievementCategory achievementCategory;
    private final String[] description;
    private final boolean secret;

    /**
     * Achievement! Keep 'em playing...
     *
     * @param plugin      Base plugin
     * @param id          Achievement ID
     * @param display     Achievement name
     * @param reward      Reward to display to them
     * @param description The achievement description
     */
    public Achievement(AssiPlugin plugin, String id, AchievementCategory achievementCategory, String display, String reward, boolean secret,
                       String... description) {
        this.plugin = plugin;
        this.id = id;
        this.achievementCategory = achievementCategory;
        this.display = (secret ? ChatColor.YELLOW + ChatColor.BOLD.toString() + "SECRET " : "") + ChatColor.GOLD + display;
        this.reward = reward;
        this.description = description;
        this.secret = secret;
        plugin.registerListener(this);
    }

    public Achievement(AssiPlugin plugin, String id, AchievementCategory achievementCategory, String display, String reward, String... description) {
        this(plugin, id, achievementCategory, display, reward, false, description);
    }

    /**
     * Code executed upon player meeting the requirements.
     *
     * @param player the player to reward.
     */
    public abstract void giveReward(AssiPlayer player);

    /**
     * Called in the UI where it shows what they have done/need to do.
     *
     * @param player the player to base data off.
     * @return A string list of what they've done.
     * i.e "Played a UHC Game"
     */
    public abstract Set<String> showProgress(AssiPlayer player);

    /**
     * Show the progress hotbar optionally.
     * <p>
     * See {@link net.assimilationmc.assicore.util.UtilMessage#sendProgressActionBar(Player, String, int, int)}
     *
     * @param assiPlayer player to base stats off.
     */
    public void showProgressHotbar(AssiPlayer assiPlayer) {
    }

    /**
     * Method called to run giveReward and call the event, bit inconvenient but w/e
     *
     * @param player Player who did it
     */
    public final void give(AssiPlayer player) {
        UtilServer.callEvent(new AchievementAchieveEvent(this, player));
        giveReward(player);
    }

    /**
     * Get the base plugin instance
     *
     * @return base plugin
     */
    public AssiPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get the base ID
     *
     * @return ID of the achievement
     */
    public String getId() {
        return id;
    }

    /**
     * @return the category of the achievement.
     */
    public AchievementCategory getAchievementCategory() {
        return achievementCategory;
    }

    /**
     * Get the display name of the achievement
     *
     * @return Display name of the achievement
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Get the reward to show off to the player
     *
     * @return Reward to show.
     */
    public String getReward() {
        return reward;
    }

    /**
     * Get the achievement description
     *
     * @return the achievement's descriptions
     */
    public String[] getDescription() {
        return description;
    }

    /**
     * @return will the achievement show before achievement
     */
    public boolean isSecret() {
        return secret;
    }

}