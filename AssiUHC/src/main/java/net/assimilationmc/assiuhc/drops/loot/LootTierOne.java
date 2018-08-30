package net.assimilationmc.assiuhc.drops.loot;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public enum LootTierOne {

    IRON_SWORD(new ItemStack(Material.IRON_SWORD), 5),
    IRON(new ItemStack(Material.IRON_INGOT), 4, 7),
    GOLDEN_APPLE(new ItemStack(Material.GOLDEN_APPLE), 3),
    FISHING_ROD(new ItemStack(Material.FISHING_ROD), 6),
    BUCKET(new ItemStack(Material.BUCKET), 7),
    STEAK(new ItemStack(Material.COOKED_BEEF), 8, 32),
    APPLE(new ItemStack(Material.APPLE), 6, 3),
    LEATHER(new ItemStack(Material.LEATHER), 7, 2),
    BLAZE_ROD(new ItemStack(Material.BLAZE_ROD), 5, 3)

    ;

    private ItemStack itemStack;
    private double chance;
    private int maxBound;

    LootTierOne(ItemStack itemStack, double chance, int maxBound) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.maxBound = maxBound;
    }

    LootTierOne(ItemStack itemStack, double chance) {
        this(itemStack, chance, 1);
    }

    public static Map<ItemStack, Double> toChanceMap() {
        Map<ItemStack, Double> chanceMap = Maps.newHashMap();
        for (LootTierOne lootTierOne : values()) {
            if (lootTierOne.getMaxBound() != 1) {
                lootTierOne.getItemStack().setAmount(Math.max(1, new Random().nextInt(lootTierOne.getMaxBound() + 1)));
            }

            chanceMap.put(lootTierOne.getItemStack(), lootTierOne.getChance());
        }
        return chanceMap;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getChance() {
        return chance;
    }

    public int getMaxBound() {
        return maxBound;
    }
}
