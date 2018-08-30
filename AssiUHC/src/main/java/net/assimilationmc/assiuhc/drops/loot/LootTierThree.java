package net.assimilationmc.assiuhc.drops.loot;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public enum LootTierThree {

    SHARP_DIAMOND_SWORD(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build(), 6),
    DIAMONDS(new ItemStack(Material.DIAMOND, 6), 7),
    ANVIL(new ItemStack(Material.ANVIL), 5),
    SHARP_TWO(new ItemBuilder(Material.ENCHANTED_BOOK).addStoredEnchant(Enchantment.DAMAGE_ALL, 2).build(), 5),
    GOD_APPLE(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 6),
    BREW_STAND(new ItemStack(Material.BREWING_STAND), 6),
    VERY_SHARP_SWORD(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).build(), 3),
    EFF_PICK(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 2).build(), 4),
    ;

    private ItemStack itemStack;
    private double chance;
    private int maxBound;

    LootTierThree(ItemStack itemStack, double chance, int maxBound) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.maxBound = maxBound;
    }

    LootTierThree(ItemStack itemStack, double chance) {
        this(itemStack, chance, 1);
    }

    public static Map<ItemStack, Double> toChanceMap() {
        Map<ItemStack, Double> chanceMap = Maps.newHashMap();
        for (LootTierThree lootTierThree : values()) {
            if (lootTierThree.getMaxBound() != 1) {
                lootTierThree.getItemStack().setAmount(Math.max(1, new Random().nextInt(lootTierThree.getMaxBound() + 1)));
            }
            chanceMap.put(lootTierThree.getItemStack(), lootTierThree.getChance());
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
