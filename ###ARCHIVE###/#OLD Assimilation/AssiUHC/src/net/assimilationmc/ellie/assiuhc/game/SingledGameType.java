package net.assimilationmc.ellie.assiuhc.game;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 27/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum SingledGameType {

    HARDCORE(4, 50, new ItemBuilder(Material.SKULL_ITEM).setDisplay("&fHardcore").
            setLore("&f", UColorChart.R+"The reason of living...").build(), "Hardcore"),
    ULTRA_HARDCORE(4, 50, new ItemBuilder(Material.SKULL_ITEM).setDurability((short)1).setDisplay("&cUltra Hardcore").
            setLore("&f", UColorChart.R+"You wish you weren't here").build(), "Ultra Hardcore"),
    ULTRA_ULTRA_HARDCORE(4, 50, new ItemBuilder(Material.DRAGON_EGG).setDisplay("&c&lUltra Ultra Hardcore").
            setLore("&f", "&7oh gawd").build(), "Ultra Ultra Hardcore"),
    RANKED(12, 60, new ItemBuilder(Material.IRON_SWORD).setDisplay("&aRanked").setLore("&7Serious game!",
            "&c&lWARNING &cLeaving before the game finishes results in a punishment",
            "&cEst game time is about "+UColorChart.VARIABLE+"30 minutes").build(), "Ranked"),
    SINGLE_TEST(1, 1000, new ItemBuilder(Material.BAKED_POTATO).setDisplay("&ctesteroni").build(), "Test mode")


    ;

    private int minPlayers;
    private int maxPlayers;

    private ItemStack itemStack;

    private String friendly;

    SingledGameType(int minPlayers, int maxPlayers, ItemStack itemStack,
                    String friendly){
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.itemStack = itemStack;
        this.friendly = friendly;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getFriendly() {
        return friendly;
    }
}
