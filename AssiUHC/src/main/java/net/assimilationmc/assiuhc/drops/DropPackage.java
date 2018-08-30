package net.assimilationmc.assiuhc.drops;

import net.assimilationmc.assiuhc.drops.loot.LootTier;
import net.assimilationmc.assiuhc.game.UHCGame;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract class DropPackage {

    private final String name;
    private UHCGame game;
    private LootTier tier;
    private double chance;

    public DropPackage(UHCGame game, String name, LootTier lootTier, double chance) {
        this.game = game;
        this.name = name;
        this.tier = lootTier;
        this.chance = chance;
    }

    public abstract void drop(Location location);

    public abstract ItemStack[] populate();

    public UHCGame getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public LootTier getTier() {
        return tier;
    }

    public void setTier(LootTier tier) {
        this.tier = tier;
    }

    public double getChance() {
        return chance;
    }

    public void playDropEffect(Location location) {
    }

}
