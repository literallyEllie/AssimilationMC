package net.assimilationmc.assiuhc.drops.loot;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public enum LootTierTwo {

    GOLDEN_APPLE(new ItemStack(Material.GOLDEN_APPLE, 2), 7),
    DIAMOND(new ItemStack(Material.DIAMOND), 3, 3),
    SHARP_BOOK(new ItemBuilder(Material.ENCHANTED_BOOK).addStoredEnchant(Enchantment.DAMAGE_ALL, 2).build(), 1),
    DIAMOND_BOOTS(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(), 4),
    DIAMOND_HELMET(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(), 4),
    ENDER_PEARL(new ItemStack(Material.ENDER_PEARL), 6, 2),
    ENCHANT_TABLE(new ItemStack(Material.ENCHANTMENT_TABLE), 3),
    GOD_APPLE(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 1),
    MAGNA_CREAM(new ItemStack(Material.MAGMA_CREAM), 8),
    BREW_STAND(new ItemStack(Material.BREWING_STAND), 4),
    FISHING_ROD(new ItemStack(Material.FISHING_ROD), 9),
    BLAZE_ROD(new ItemStack(Material.BLAZE_ROD), 7),
    EFF_BOOK(new ItemBuilder(Material.ENCHANTED_BOOK).addStoredEnchant(Enchantment.DIG_SPEED, 1).build(), 3),
    ANVIL(new ItemStack(Material.ANVIL), 2),

    ;

    private ItemStack itemStack;
    private double chance;
    private int maxBound;

    LootTierTwo(ItemStack itemStack, double chance, int maxBound) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.maxBound = maxBound;
    }

    LootTierTwo(ItemStack itemStack, double chance) {
        this(itemStack, chance, 1);
    }

    public static Map<ItemStack, Double> toChanceMap() {
        Map<ItemStack, Double> chanceMap = Maps.newHashMap();
        for (LootTierTwo lootTierTwo : values()) {
            if (lootTierTwo.getMaxBound() != 1) {
                lootTierTwo.getItemStack().setAmount(Math.max(1, new Random().nextInt(lootTierTwo.getMaxBound() + 1)));
            }
            chanceMap.put(lootTierTwo.getItemStack(), lootTierTwo.getChance());
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
