package net.assimilationmc.assiuhc.game.custom;

import com.google.common.collect.Lists;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import org.bukkit.Material;

import java.util.List;

public class CustomizationProperties {

    enum IntegerProperty implements CustomizationProperty<Integer> {

        WARMUP_TIME(-2), // done by map
        MAX_HEATH(20),

        ;

        private final int defaultValue;

        IntegerProperty(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String id() {
            return name();
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

        EXPLOSIONS(true),
        // HORSES(false, Lists.newArrayList(UHCGameSubType.TEAMED_CLASSIC, UHCGameSubType.TEAMED_SCATTER, UHCGameSubType.SINGLES_CLASSIC)),
        MOBS(true, Lists.newArrayList(UHCGameSubType.SINGLES_DEATHMATCH, UHCGameSubType.TEAMED_DEATHMATCH)),
        CRATES(true),
        NON_VANILLA_RECIPES(true),
        QUICK_SMELT(true),

        ;

        private final boolean defaultValue;
        private final List<UHCGameSubType> exceptions;

        BooleanProperty(boolean defaultValue, List<UHCGameSubType> exceptions) {
            this.defaultValue = defaultValue;
            this.exceptions = exceptions;
        }

        BooleanProperty(boolean defaultValue) {
            this(defaultValue, Lists.newArrayList());
        }

        @Override
        public String id() {
            return name();
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

}
