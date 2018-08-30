package net.assimilationmc.assicore.booster;

import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Booster {

    private final String id, pretty, description;
    private final BoosterType boosterType;
    private boolean enabled;
    private long length;
    private ItemStack itemStack;
    private int price;

    public Booster(String id, String pretty, String description, BoosterType boosterType, long length, int price) {
        this.id = id;
        this.pretty = boosterType.getChatColor() + ChatColor.BOLD.toString() + pretty;
        this.description = description;
        this.boosterType = boosterType;
        this.enabled = false;
        this.length = length;
        this.price = price;

        this.itemStack = new ItemBuilder(Material.INK_SACK).setDisplay(this.pretty)
                .setColor(boosterType.getColor())
                .setLore(C.C, C.V + description, C.C).build();
    }

    public int processBucks(int in) {
        return in;
    }

    public int processUC(int in) {
        return in;
    }

    public int processUHCXp(int in) {
        return in;
    }

    public String getId() {
        return id;
    }

    public String getPretty() {
        return pretty;
    }

    public String getDescription() {
        return description;
    }

    public BoosterType getBoosterType() {
        return boosterType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return id;
    }
}
