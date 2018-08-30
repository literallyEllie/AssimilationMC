package net.assimilationmc.assicore.cosmetic.cosmetics.inv;

import net.assimilationmc.assicore.cosmetic.Cosmetic;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticFighter extends Cosmetic {

    private ItemStack[] itemStacks;

    public CosmeticFighter() {
        super(CosmeticType.FIGHTER, "Foes will shudder in fear to all whom apply it", Rank.DEMONIC);

        this.itemStacks = new ItemStack[]{
                new ItemBuilder(Material.DIAMOND_BOOTS).setDisplay(ChatColor.BLUE + "Fighter boots").build(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).setDisplay(ChatColor.BLUE + "Fighter leggings").build(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplay(ChatColor.BLUE + "Fighter chestplate").build(),
                new ItemBuilder(Material.DIAMOND_HELMET).setDisplay(ChatColor.BLUE + "Fighter helmet").build(),
        };

    }

    @Override
    public void apply(Player player) {
        player.closeInventory();
        player.getInventory().setArmorContents(itemStacks);
    }

    @Override
    public void remove(Player player) {
        player.getInventory().setArmorContents(null);
    }

}
