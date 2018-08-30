package net.assimilationmc.assicore.achievement.achievements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilMath;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class CmdAchievements extends AssiCommand {

    private UI ui;

    public CmdAchievements(AssiPlugin plugin) {
        super(plugin, "achievements", "Achievements", Lists.newArrayList("achievement", "missions", "quests", "ach"));
        requirePlayer();

        ui = new UI(plugin, ChatColor.GREEN + "Achievement Categories", 9) {

            @Override
            public void open(Player player) {
                removeAllButtons();

                Set<String> achievementIds = getPlugin().getPlayerManager().getPlayer(player).getAchievements().keySet();

                Map<AchievementCategory, Map<String, Achievement>> allAchievements = getPlugin().getAchievementManager().getAchievements();
                Map<AchievementCategory, Map<Achievement, Integer>> achieved = Maps.newHashMap();

                for (AchievementCategory achievementCategory : AchievementCategory.values()) {
                    achieved.put(achievementCategory, Maps.newHashMap());
                }

                for (String achievementId : achievementIds) {
                    Achievement achievement = getPlugin().getAchievementManager().getAchievement(achievementId);
                    if (achievement == null) return;
                    achieved.get(achievement.getAchievementCategory()).put(achievement, achieved.get(achievement.getAchievementCategory()).size() + 1);
                }


                for (Map.Entry<AchievementCategory, Map<String, Achievement>> achievementCategoryMapEntry : allAchievements.entrySet()) {
                    final AchievementCategory category = achievementCategoryMapEntry.getKey();

                    int done = achieved.get(category).size();
                    int toDo = allAchievements.get(category).size();

                    addButton(new Button() {
                        @Override
                        public ItemStack getItemStack(AssiPlayer clicker) {
                            final boolean allDone = done == toDo;

                            final ItemBuilder itemBuilder = new ItemBuilder(category.getMaterial())
                                    .setDisplay(ChatColor.GOLD + category.getPretty())
                                    .setLore(C.C, (allDone ? ChatColor.GREEN + ChatColor.BOLD.toString() + "ALL DONE!" : C.II + "Progress: " +
                                                    ChatColor.AQUA + done + "/" + toDo + C.C + ChatColor.ITALIC + " " + UtilMath.prettyPercentage(done, toDo) + "%"),
                                            C.C, C.V + "Click " + C.II + "to view sub-achievements.");
                            if (allDone) {
                                itemBuilder.addGlow();
                            }

                            return itemBuilder.build();
                        }

                        @Override
                        public void onAction(AssiPlayer clicker, ClickType clickType) {
                            // open sub
                            new UIAchievements(getPlugin(), category, achieved.get(category).keySet(), clicker).open(clicker.getBase());
                        }
                    });

                    super.open(player);
                }

            }
        };

    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        ui.open((Player) sender);
    }

}
