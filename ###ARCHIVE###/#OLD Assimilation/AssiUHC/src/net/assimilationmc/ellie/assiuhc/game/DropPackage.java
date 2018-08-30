package net.assimilationmc.ellie.assiuhc.game;

import org.bukkit.Location;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class DropPackage {

    private Location location;

    public DropPackage(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
