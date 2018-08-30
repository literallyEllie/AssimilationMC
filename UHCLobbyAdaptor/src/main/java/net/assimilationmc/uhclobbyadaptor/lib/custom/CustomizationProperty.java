package net.assimilationmc.uhclobbyadaptor.lib.custom;

import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import org.bukkit.Material;

public interface CustomizationProperty<T> {

    String id();

    Material getMaterial();

    T getDefaultValue();

    T getDefaultValue(UHCGameSubType subType);

}
