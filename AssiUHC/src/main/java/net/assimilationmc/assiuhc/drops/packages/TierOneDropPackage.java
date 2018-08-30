package net.assimilationmc.assiuhc.drops.packages;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilRandom;
import net.assimilationmc.assicore.util.UtilTime;
import net.assimilationmc.assiuhc.drops.TieredDropPackage;
import net.assimilationmc.assiuhc.drops.loot.LootTier;
import net.assimilationmc.assiuhc.drops.loot.LootTierOne;
import net.assimilationmc.assiuhc.game.UHCGame;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TierOneDropPackage extends TieredDropPackage {

    public TierOneDropPackage(UHCGame game) {
        super(game, "Tier One Drop Package", LootTier.ONE, 0.7f);
    }

    @Override
    public ItemStack[] populate() {
        final Map<ItemStack, Double> items = LootTierOne.toChanceMap();

        int maxItems = 8;
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
            if (UtilRandom.randomNumber(5) < 2) break;
        }

        return retItems.toArray(new ItemStack[0]);
    }

}
