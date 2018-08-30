package net.assimilationmc.assicore.world;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.SerializedLocation;
import org.bukkit.Location;

import java.util.Map;

public class WorldData {

    private String name;
    private Map<String, SerializedLocation> spawns;
    private Map<String, AssiRegion> regions;

    /**
     * World Data for a world.
     *
     * @param name the name of the world.
     */
    public WorldData(String name) {
        this.name = name;
        this.spawns = Maps.newHashMap();
        this.regions = Maps.newHashMap();
    }

    /**
     * @return the world name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return all the spawns in the map.
     */
    public Map<String, SerializedLocation> getSpawns() {
        return spawns;
    }

    /**
     * Add a spawn.
     *
     * @param id       the spawn id.
     * @param location the location of the spawn.
     */
    public void addSpawn(String id, Location location) {
        addSpawn(id, new SerializedLocation(location, true));
    }

    /**
     * Add a spawn.
     *
     * @param id       the spawn id.
     * @param location the serialized location from a config.
     */
    public void addSpawn(String id, SerializedLocation location) {
        this.spawns.put(id, location);
    }

    /**
     * Remove a spawn.
     *
     * @param id the spawn id.
     */
    public void removeSpawn(String id) {
        spawns.remove(id);
    }

    /**
     * @return Get the regions of the world.
     */
    public Map<String, AssiRegion> getRegions() {
        return regions;
    }

    /**
     * Add a region to the world.
     *
     * @param id     the region id.
     * @param region the region itself.
     */
    public void addRegion(String id, AssiRegion region) {
        this.regions.put(id.toLowerCase(), region);
    }

    /**
     * Remove a region from a world.
     *
     * @param id the region id.
     */
    public void removeRegion(String id) {
        this.regions.remove(id.toLowerCase());
    }

}
