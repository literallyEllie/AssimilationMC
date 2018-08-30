package net.assimilationmc.assicore.joinitems;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class JoinItem implements Listener {

    private int slot;
    private ItemStack itemStack;
    private ItemGiveCondition giveCondition;

    public JoinItem(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemGiveCondition getGiveCondition() {
        return giveCondition;
    }

    public void setGiveCondition(ItemGiveCondition giveCondition) {
        this.giveCondition = giveCondition;
    }

    public boolean hasGiveCondition() {
        return giveCondition != null;
    }

    public abstract void onClick(Player player);

}
