package net.assimilationmc.assicore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SerializedLocation {

    private final static String SEPARATOR = ";";

    private final String world;
    private final int x, y, z;
    private float yaw = 0, pitch = 0;

    /**
     * Create a serialized location. This can be used for as storage and also supports decimalization.
     * This constructor is for locations.
     *
     * @param world the world of the location.
     * @param x     the x coordinate.
     * @param y     the y coordinate.
     * @param z     the z coordinate.
     */
    public SerializedLocation(World world, int x, int y, int z, float yaw, float pitch) {
        this(world.getName(), x, y, z, yaw, pitch);
    }

    /**
     * Create a serialized player spawn. This can be used as storage and this also supports deserialization
     * This constructor is for locations.
     *
     * @param world the world name.
     * @param x     the x coordinate.
     * @param y     the y coordinate.
     * @param z     the z coordinate.
     * @param yaw   the yaw of the location.
     * @param pitch the pitch of the location.
     */
    public SerializedLocation(String world, int x, int y, int z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Create a serialized location. This can be used for as storage and also supports decimalization.
     * This constructor is for blocks.
     *
     * @param world the world of the location.
     * @param x     the x coordinate.
     * @param y     the y coordinate.
     * @param z     the z coordinate.
     */
    public SerializedLocation(World world, int x, int y, int z) {
        this(world.getName(), x, y, z, 0, 0);
    }

    /**
     * Create a serialized location. This can be used for as storage and also supports decimalization.
     * This constructor is for blocks.
     *
     * @param world the world name.
     * @param x     the x coordinate.
     * @param y     the y coordinate.
     * @param z     the z coordinate.
     */
    public SerializedLocation(String world, int x, int y, int z) {
        this(world, x, y, z, 0, 0);
    }

    /**
     * Create a serialized location from an existing location.
     *
     * @param location the location.
     * @param yawPitch should the yaw and pitch be saved as well?
     */
    public SerializedLocation(Location location, boolean yawPitch) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (yawPitch) {
            yaw = location.getYaw();
            pitch = location.getPitch();
        }
    }

    /**
     * Deserialize the location and return it as a Serialized Location
     *
     * @param loc The serialized location as a string.
     * @return the deserialize'd location.
     */
    public static SerializedLocation deserialize(String loc) {
        final String[] args = loc.split(SEPARATOR);
        final int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[2]),
                z = Integer.parseInt(args[3]);
        float yaw = 0f, pitch = 0f;
        if (args.length > 4) {
            yaw = Float.parseFloat(args[4]);
            pitch = Float.parseFloat(args[5]);
        }
        return new SerializedLocation(args[0], x, y, z, yaw, pitch);
    }

    public World getCBWorld() {
        return Bukkit.getWorld(world);
    }

    /**
     * @return the string name of the world.
     */
    public String getWorld() {
        return world;
    }

    /**
     * @return the x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * @return the z coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * @return the yaw of the location.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * @return the pitch of the location.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * @return creates a Location object from the parameters provided.
     */
    public Location toLocation() {
        return new Location(getCBWorld(), x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return world + SEPARATOR + x + SEPARATOR + y + SEPARATOR + z + (yaw == 0f &&
                pitch == 0f ? "" : SEPARATOR + yaw + SEPARATOR + pitch);
    }

}
