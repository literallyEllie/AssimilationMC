package net.assimilationmc.assicore.ui;

import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface Button {

    ItemStack getItemStack(AssiPlayer clicker);

    void onAction(AssiPlayer clicker, ClickType clickType);

}
