package net.assimilationmc.assiuhc.game.custom;

import net.assimilationmc.assiuhc.game.UHCGameSubType;

public interface CustomizationProperty<T> {

    String id();

    T getDefaultValue();

    T getDefaultValue(UHCGameSubType subType);

}
