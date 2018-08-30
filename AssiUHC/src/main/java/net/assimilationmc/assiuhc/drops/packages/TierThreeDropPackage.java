package net.assimilationmc.assiuhc.drops.packages;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilRandom;
import net.assimilationmc.assicore.util.UtilTime;
import net.assimilationmc.assiuhc.drops.TieredDropPackage;
import net.assimilationmc.assiuhc.drops.loot.LootTier;
import net.assimilationmc.assiuhc.drops.loot.LootTierThree;
import net.assimilationmc.assiuhc.game.UHCGame;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TierThreeDropPackage extends TieredDropPackage {

    public TierThreeDropPackage(UHCGame game) {
        super(game, "Tier Three Drop Package", LootTier.THREE, 0.3f);
    }

    @Override
    public ItemStack[] populate() {
        final Map<ItemStack, Double> items = LootTierThree.toChanceMap();

        int maxItems = 3;
        List<ItemStack> retItems = Lists.newArrayList();

        long start = System.currentTimeMillis();
        while (retItems.size() <= maxItems) {
            if (UtilTime.elapsed(start, TimeUnit.MICROSECONDS.toMillis(500))) {
                D.d("breaking of timeout");
                break;
            }
            final ItemStack itemStack = UtilRandom.selectWeightedRandomNew(items);
            if (retItems.contains(itemStack)) continue;
            retItems.add(itemStack);
            if (UtilRandom.randomNumber(3) < 2) break;
        }

        return retItems.toArray(new ItemStack[0]);
    }

    @Override
    public void playDropEffect(Location location) {
        location.getWorld().strikeLightning(location);
    }

}
