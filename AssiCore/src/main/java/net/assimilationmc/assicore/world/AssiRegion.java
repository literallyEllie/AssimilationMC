package net.assimilationmc.assicore.world;

import net.assimilationmc.assicore.util.AssiVector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class AssiRegion {

    private String world;
    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    public AssiRegion(Location minLocation) {
        this(minLocation, new Location(minLocation.getWorld(), 0, 0, 0));
    }

    public AssiRegion(Location minLocation, Location maxLocation) {
        if (minLocation.getWorld() != maxLocation.getWorld())
            throw new IllegalArgumentException("minLocation and maxLocation must be in the same world!");
        this.world = minLocation.getWorld().getName();
        this.minX = minLocation.getBlockX();
        this.minY = minLocation.getBlockY();
        this.minZ = minLocation.getBlockZ();
        this.maxX = maxLocation.getBlockX();
        this.maxY = maxLocation.getBlockY();
        this.maxZ = maxLocation.getBlockZ();
    }

    public AssiRegion(String location) {
        final String[] args = location.split(";#;");
        this.world = args[0];
        this.minX = Integer.parseInt(args[1]);
        this.minY = Integer.parseInt(args[2]);
        this.minZ = Integer.parseInt(args[3]);
        this.maxX = Integer.parseInt(args[4]);
        this.maxY = Integer.parseInt(args[5]);
        this.maxZ = Integer.parseInt(args[6]);
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
        return world + ";#;" + minX + ";#;" + minY + ";#;" + minZ + ";#;" + maxX + ";#;" + maxY + ";#;" + maxZ;
    }

    public boolean intersects(Location location, boolean ignoreWorld) {
        AssiVector pl = new AssiVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        AssiVector min = new AssiVector(Math.min(minX, maxX), Math.min(minY, maxY), Math.min(minZ, maxZ));
        AssiVector max = new AssiVector(Math.max(minX, maxX), Math.max(minY, maxY), Math.max(minZ, maxZ));
        return (ignoreWorld || location.getWorld().equals(Bukkit.getWorld(world))) && pl.isInAABB(min, max);
    }

    public boolean containsEntity(Entity entity) {
        return intersects(entity.getLocation(), false);
    }

}
