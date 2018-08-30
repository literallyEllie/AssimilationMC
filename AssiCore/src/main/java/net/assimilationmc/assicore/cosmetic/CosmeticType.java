package net.assimilationmc.assicore.cosmetic;

import net.assimilationmc.assicore.cosmetic.cosmetics.inv.CosmeticBow;
import net.assimilationmc.assicore.cosmetic.cosmetics.inv.CosmeticFighter;
import net.assimilationmc.assicore.cosmetic.cosmetics.particle.CosmeticDiamonds;
import net.assimilationmc.assicore.cosmetic.cosmetics.particle.CosmeticLove;
import org.bukkit.Material;

public enum CosmeticType {

    // Particles
    LOVE("Love", Material.COOKIE, CosmeticLove.class),
    DIAMONDS("Diamonds", Material.DIAMOND, CosmeticDiamonds.class),

    // Inventory
    FIGHTER("Fighter", Material.DIAMOND_HELMET, CosmeticFighter.class),
    BOW("Teleport Bow", Material.BOW, CosmeticBow.class),;

    private final String prettyName;
    private final Material material;
    private final Class clazz;

    CosmeticType(String prettyName, Material material, Class<? extends Cosmetic> clazz) {
        this.prettyName = prettyName;
        this.material = material;
        this.clazz = clazz;
    }

    public static CosmeticType fromInput(String input) {
        input = input.toUpperCase();

        for (CosmeticType cosmeticType : values()) {
            if (cosmeticType.name().equalsIgnoreCase(input) ||
                    cosmeticType.getPrettyName().equalsIgnoreCase(input))
                return cosmeticType;
        }

        return null;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public Material getMaterial() {
        return material;
    }

    public Class getClazz() {
        return clazz;
    }

}
