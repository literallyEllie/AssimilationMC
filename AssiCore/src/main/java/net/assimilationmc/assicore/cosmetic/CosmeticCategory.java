package net.assimilationmc.assicore.cosmetic;

import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum CosmeticCategory {

    ARMOR(new ItemBuilder(Material.IRON_CHESTPLATE).build()),
    PARTICLE(new ItemBuilder(Material.NETHER_STAR).build());

    private ItemStack itemStack;

    CosmeticCategory(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

}
