package net.assimilationmc.assicore.cosmetic.cosmetics.particle;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.cosmetic.Cosmetic;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CosmeticDiamonds extends Cosmetic {

    private AssiPlugin plugin;

    public CosmeticDiamonds(AssiPlugin plugin) {
        super(CosmeticType.DIAMONDS, "Diamonds! Diamonds! Diamonds everywhere!");
        this.plugin = plugin;
    }

    @Override
    public void tick(Location center) {
        final ItemStack stack = new ItemStack(Material.DIAMOND);
        for (int i = 0; i < new Random().nextInt(8) + 1; i++) {
            final Item item = center.getWorld().dropItemNaturally(center.clone().add(0, 0.5, 1), stack);
            item.setVelocity(item.getVelocity().setY(0.3));
            item.setPickupDelay(2000);

            plugin.getServer().getScheduler().runTaskLater(plugin, item::remove, 40L);
        }
    }

}
