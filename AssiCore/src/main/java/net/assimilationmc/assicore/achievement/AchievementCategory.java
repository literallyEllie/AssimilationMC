package net.assimilationmc.assicore.achievement;

import com.google.common.collect.Sets;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.SortedSet;

public enum AchievementCategory {

    GAME_PLAY("Game Play", Material.DIAMOND_CHESTPLATE),
    ECONOMIC("Economic", Material.DIAMOND),
    SOCIAL("Social", Material.YELLOW_FLOWER),
    MISC("Miscellaneous", Material.BONE),
    SECRET("Secret", Material.NETHER_STAR);

    private String pretty;
    private Material material;

    AchievementCategory(String pretty, Material material) {
        this.pretty = pretty;
        this.material = material;
    }

    public static SortedSet<AchievementCategory> sort() {
        SortedSet<AchievementCategory> sorted = Sets.newTreeSet();
        sorted.addAll(Arrays.asList(values()));
        return sorted;
    }

    public String getPretty() {
        return pretty;
    }

    public Material getMaterial() {
        return material;
    }

}
