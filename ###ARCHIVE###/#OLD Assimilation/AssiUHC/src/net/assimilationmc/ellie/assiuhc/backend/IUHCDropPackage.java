package net.assimilationmc.ellie.assiuhc.backend;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Ellie on 31/12/2016 for AssimilationMC.
 * Affiliated with www.minevelop.com
 */
public interface IUHCDropPackage {

    void drop();
    void fill(Block block);
    Location getLocation();
    HashMap<ItemStack, Double> getItems();
    void setItems(HashMap<ItemStack, Double> items);
    String getType();

}
