package net.assimilationmc.assicore.achievement;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.achievement.achievements.CmdAchievements;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;

public class AchievementManager extends Module {

    private Map<AchievementCategory, Map<String, Achievement>> achievements;

    /***
     * Achievement manager where all the achievements are registered
     *
     * @param plugin Core plugin instance.
     */
    public AchievementManager(AssiPlugin plugin) {
        super(plugin, "Achievements");
    }

    @Override
    protected void start() {
        achievements = Maps.newLinkedHashMap();

        for (AchievementCategory achievementCategory : AchievementCategory.values()) {
            achievements.put(achievementCategory, Maps.newLinkedHashMap());
        }

        new Reflections("net.assimilationmc.assicore.achievement.achievements").getSubTypesOf(Achievement.class).forEach(aClass -> {
            try {
                addAchievement((Achievement) aClass.getConstructors()[0].newInstance(getPlugin()));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException e) {
                if (!e.getMessage().equals("wrong number of arguments")) {
                    log(Level.WARNING, "Failed to load achievement " + aClass.getSimpleName());
                    e.printStackTrace();
                }
            }
        });

        getPlugin().getCommandManager().registerCommand(new CmdAchievements(getPlugin()));

    }

    @Override
    protected void end() {
        achievements.clear();
    }

    public Map<AchievementCategory, Map<String, Achievement>> getAchievements() {
        return achievements;
    }

    /**
     * Get an achievement by id.
     *
     * @param id Achievement String ID
     * @return The achievement mapped to the provided ID.
     */
    public Achievement getAchievement(String id) {

        for (Map.Entry<AchievementCategory, Map<String, Achievement>> achievementCategoryMapEntry : achievements.entrySet()) {
            for (Map.Entry<String, Achievement> stringAchievementEntry : achievementCategoryMapEntry.getValue().entrySet()) {
                if (stringAchievementEntry.getKey().equalsIgnoreCase(id))
                    return stringAchievementEntry.getValue();
            }
        }

        return null;
    }

    public Map<String, Achievement> getAchievements(AchievementCategory category) {
        return achievements.get(category);
    }

    /**
     * Register an achievement.
     *
     * @param achievement Achievement to register
     */
    public void addAchievement(Achievement achievement) {
        achievements.get(achievement.getAchievementCategory()).put(achievement.getId().toUpperCase(), achievement);
    }

    /**
     * Unregister an achievement
     *
     * @param achievement Achievement to unregister
     */
    public void unregisterAchievement(Achievement achievement) {
        achievements.get(achievement.getAchievementCategory()).remove(achievement.getId());
    }

    /**
     * Unregister an achievement.
     *
     * @param achievementCategory the category its in.
     * @param id                  the id.
     */
    public void unregisterAchievement(AchievementCategory achievementCategory, String id) {
        achievements.get(achievementCategory).remove(id.toUpperCase());
    }

    @EventHandler
    public void on(final AchievementAchieveEvent e) {
        final Achievement achievement = e.getAchievement();
        final AssiPlayer player = e.getPlayer();

        player.clearProgress(achievement.getId());

        final Player base = player.getBase();
        if (base == null) return;

        if (!player.getAchievements().containsKey(achievement.getId())) {
            base.playSound(base.getLocation(), Sound.LEVEL_UP, 50, 30);
            base.sendMessage(C.C);
            base.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "                       ACHIEVEMENT GET!");
            base.sendMessage(ChatColor.GOLD + (achievement.isSecret() ? "        " : "") + "     " + achievement.getDisplay() + C.C + " - " + C.V + Joiner.on("").join(achievement.getDescription()));
            base.sendMessage(C.C);

            player.addAchievement(achievement.getId());
        }

    }

}