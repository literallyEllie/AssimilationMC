package com.assimilation.ellie.assicore.api;

import org.bukkit.Location;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiRegion {

    private String world;
    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    public AssiRegion(Location minLocation){
        this(minLocation, new Location(minLocation.getWorld(), 0, 0, 0));
    }

    public AssiRegion(Location minLocation, Location maxLocation){
        if(minLocation.getWorld() != maxLocation.getWorld()) throw new IllegalArgumentException("minLocation and maxLocation must be in the same world!");
        this.world = minLocation.getWorld().getName();
        this.minX = minLocation.getBlockX();
        this.minY = minLocation.getBlockY();
        this.minZ = minLocation.getBlockZ();
        this.maxX = maxLocation.getBlockX();
        this.maxY = maxLocation.getBlockY();
        this.maxZ = maxLocation.getBlockZ();
    }

    public AssiRegion(String location) {
        String[] args = location.split(";#;");
        world = args[0];
        minX = Integer.parseInt(args[1]);
        minY = Integer.parseInt(args[2]);
        minZ = Integer.parseInt(args[3]);
        maxX = Integer.parseInt(args[4]);
        maxY = Integer.parseInt(args[5]);
        maxZ = Integer.parseInt(args[6]);
    }

    public String getWorld() {
        return world;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    @Override
    public String toString() {
        return world+";#;"+minX+";#;"+minY+";#;"+minZ+";#;"+maxZ+";#;"+maxY+";#;"+maxZ;
    }
}
