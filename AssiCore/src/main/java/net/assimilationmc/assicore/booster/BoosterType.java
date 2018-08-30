package net.assimilationmc.assicore.booster;

import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;

public enum BoosterType {

    BUCKS(ItemBuilder.StackColor.GREEN),
    ULTRA_COIN(ItemBuilder.StackColor.ORANGE),
    UHC_XP(ItemBuilder.StackColor.BLUE),;

    private ItemBuilder.StackColor color;

    BoosterType(ItemBuilder.StackColor stackColor) {
        this.color = stackColor;
    }

    public ItemBuilder.StackColor getColor() {
        return color;
    }

    public ChatColor getChatColor() {
        return color.getChatColor();
    }

}
