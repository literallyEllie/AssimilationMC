package net.assimilationmc.uhclobbyadaptor.lib.custom;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import javafx.util.Pair;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import org.bukkit.Material;

import java.util.List;

public class CustomizationProperties {

    enum IntegerProperty implements CustomizationProperty<Integer> {

        WARMUP_TIME(Material.IRON_SWORD, -2), // done by map
        MAX_HEATH(Material.RED_ROSE, 20),

        ;

        private final Material material;
        private final int defaultValue;

        IntegerProperty(Material material, int defaultValue) {
            this.material = material;
            this.defaultValue = defaultValue;
        }

        @Override
        public String id() {
            return name();
        }

        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public Integer getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Integer getDefaultValue(UHCGameSubType subType) {
            return defaultValue;
        }
    }

    enum BooleanProperty implements CustomizationProperty<Boolean> {

        EXPLOSIONS(Material.TNT, true),
        // HORSES(Material.GOLD_BARDING, false, Lists.newArrayList(UHCGameSubType.TEAMED_CLASSIC, UHCGameSubType.TEAMED_SCATTER, UHCGameSubType.SINGLES_CLASSIC)),
        MOBS(Material.SKULL_ITEM, true, Lists.newArrayList(UHCGameSubType.SINGLES_DEATHMATCH, UHCGameSubType.TEAMED_DEATHMATCH)),
        CRATES(Material.CHEST, true),
        NON_VANILLA_RECIPES(Material.GOLDEN_APPLE, true),
        QUICK_SMELT(Material.FURNACE, true),

        ;

        private final Material material;
        private final boolean defaultValue;
        private final List<UHCGameSubType> exceptions;

        BooleanProperty(Material material, boolean defaultValue, List<UHCGameSubType> exceptions) {
            this.material = material;
            this.defaultValue = defaultValue;
            this.exceptions = exceptions;
        }

        BooleanProperty(Material material, boolean defaultValue) {
            this (material, defaultValue, Lists.newArrayList());
        }

        @Override
        public String id() {
            return name();
        }

        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public Boolean getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Boolean getDefaultValue(UHCGameSubType subType) {
            for (UHCGameSubType exception : exceptions) {
                if (exception == subType)
                    return !defaultValue;
            }
            return defaultValue;
        }

    }

    public static final CustomizationProperty<Integer> WARMUP_TIME = IntegerProperty.WARMUP_TIME;
    public static final CustomizationProperty<Integer> MAX_HEALTH = IntegerProperty.MAX_HEATH;

    public static final CustomizationProperty<Boolean> EXPLOSIONS = BooleanProperty.EXPLOSIONS;
    // public static final CustomizationProperty<Boolean> HORSES = BooleanProperty.HORSES;
    public static final CustomizationProperty<Boolean> MOBS = BooleanProperty.MOBS;
    public static final CustomizationProperty<Boolean> CRATES = BooleanProperty.CRATES;
    public static final CustomizationProperty<Boolean> NON_VANILLA_RECIPES = BooleanProperty.NON_VANILLA_RECIPES;
    public static final CustomizationProperty<Boolean> QUICK_SMELT = BooleanProperty.QUICK_SMELT;

    public static CustomizationProperty get(String key) {

        for (IntegerProperty integerProperty : IntegerProperty.values()) {
            if (integerProperty.name().equalsIgnoreCase(key))
                return integerProperty;
        }

        for (BooleanProperty booleanProperty : BooleanProperty.values()) {
            if (booleanProperty.name().equalsIgnoreCase(key))
                return booleanProperty;
        }

        return null;
    }

    public static CustomizationProperty[] values() {
        return ObjectArrays.concat(BooleanProperty.values(), IntegerProperty.values(), CustomizationProperty.class);
    }

}
