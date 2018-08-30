package net.assimilationmc.assicore.achievement.achievements;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public class UIAchievements extends UI {

    public UIAchievements(AssiPlugin plugin, AchievementCategory achievementCategory, Set<Achievement> achieved, AssiPlayer player) {
        super(plugin, C.C + "Categories " + C.SS + ChatColor.GOLD + achievementCategory.getPretty(), 36);

        Collection<Achievement> allAchievements = getPlugin().getAchievementManager().getAchievements().get(achievementCategory).values();

        if (achievementCategory == AchievementCategory.SECRET && achieved.isEmpty()) {

            addButton(13, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.WEB).setDisplay(C.C)
                            .setLore(C.C + ChatColor.ITALIC + "Shhh, its a secret!", C.C).build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });

        }

        for (Achievement achievement : allAchievements) {
            if (achievement.isSecret() && !player.hasAchievement(achievement.getId())) continue;

            boolean done = achieved.contains(achievement);

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    final ItemBuilder item = new ItemBuilder(done ? Material.EMERALD_BLOCK : Material.IRON_BLOCK)
                            .setDisplay((done ? ChatColor.GOLD : C.II) + achievement.getDisplay());

                    if (achievement.isSecret()) {
                        item.setType(Material.DIAMOND_BLOCK);
                    }

                    if (done) {
                        item.addGlow();
                    }

                    item.appendLore(C.C);
                    for (String s : achievement.getDescription()) {
                        item.appendLore(C.C + s);
                    }

                    item.appendLore(C.C);

                    item.appendLore(ChatColor.AQUA + "Reward: " + C.V + achievement.getReward());

                    item.appendLore(C.C);

                    if (done) {
                        item.appendLore(ChatColor.GREEN + ChatColor.BOLD.toString() + "Completed " +
                                UtilTime.formatTimeStamp(player.getAchievements().get(achievement.getId())));
                    } else {
                        achievement.showProgress(player).forEach(item::appendLore);
                    }

                    item.appendLore(C.C);

                    return item.build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });

        }

        addButton(35, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.II + "Back").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                closeInventory(player.getBase());
                player.getBase().chat("/achievements");
            }
        });

    }

}
