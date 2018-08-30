package com.assimilation.ellie.assicore.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SerializableLocation {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public SerializableLocation(Location location){
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public SerializableLocation(String world, double x, double y, double z, float yaw, float pitch){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public SerializableLocation(String seralised){
        String[] a = seralised.split(";#;");
        this.world = a[0];
        this.x = Double.parseDouble(a[1]);
        this.y = Double.parseDouble(a[2]);
        this.z = Double.parseDouble(a[3]);
        this.yaw = Float.parseFloat(a[4]);
        this.pitch = Float.parseFloat(a[5]);
    }

    public Location toLocation(){
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public String toString(){
        return world+";#;"+x+";#;"+y+";#;"+z+";#;"+yaw+";#;"+pitch;
    }

}
