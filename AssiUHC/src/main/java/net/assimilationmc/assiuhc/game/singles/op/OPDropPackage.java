package net.assimilationmc.assiuhc.game.singles.op;

import net.assimilationmc.assiuhc.drops.TieredDropPackage;
import net.assimilationmc.assiuhc.drops.loot.LootTier;
import net.assimilationmc.assiuhc.game.UHCGame;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class OPDropPackage extends TieredDropPackage {

    public OPDropPackage(UHCGame game) {
        super(game, "OP Drop Package", LootTier.CUSTOM, 0.8d);
    }

    @Override
    public ItemStack[] populate() {
        return new ItemStack[0];
    }

    @Override
    public void drop(Location location) {

    }


}
