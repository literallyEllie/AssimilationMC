package net.assimilationmc.assicore.world;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.SerializedLocation;
import net.assimilationmc.assicore.world.cmd.*;
import net.assimilationmc.assicore.world.waypoint.CmdSpawnImport;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class WorldManager extends Module {

    private Map<String, WorldData> worldDataMap;
    private File coreDirectory;

    public WorldManager(AssiPlugin plugin) {
        super(plugin, "World Manager");
    }

    @Override
    protected void start() {
        this.worldDataMap = Maps.newHashMap();

        this.coreDirectory = new File(System.getProperty("user.dir"));
        loadWorlds();

        getPlugin().getCommandManager().registerCommand(new CmdSpawns(this), new CmdRegions(this),
                new CmdLoadWorld(getPlugin()), new CmdUnloadWorld(getPlugin()), new CmdPreserveWorld(getPlugin()), new CmdSpawnImport(getPlugin()));

    }

    @Override
    protected void end() {
        worldDataMap.values().forEach(this::saveData);
        worldDataMap.clear();
    }

    @EventHandler
    public void on(final WorldLoadEvent e) {
        World world = e.getWorld();
        if (worldDataMap.containsKey(world.getName())) return;
        WorldData worldData = loadData(new File(getWorldDirectory(world.getName()), "data.yml"));
        if (worldData != null) {
            worldDataMap.put(world.getName(), worldData);
        }
    }

    @EventHandler
    public void on(final WorldUnloadEvent e) {
        final World world = e.getWorld();
        if (!worldDataMap.containsKey(world.getName())) return;
        saveData(worldDataMap.get(world.getName()));
        worldDataMap.remove(world.getName());
    }

    private void loadWorlds() {
        for (File worldDir : coreDirectory.listFiles()) {
            if (!isWorld(worldDir)) continue;

            WorldData worldData;

            File worldDataFile = new File(worldDir, "data.yml");
            if (!worldDataFile.exists()) {
                worldData = createData(worldDir.getName(), worldDataFile);
            } else worldData = loadData(worldDataFile);

            worldDataMap.put(worldData.getName(), worldData);
        }

    }

    public WorldData createData(String worldName, File worldDataFile) {
        if (!worldDataFile.exists()) {
            try {
                if (!worldDataFile.createNewFile()) throw new IOException();
            } catch (IOException e) {
                getPlugin().getLogger().warning("Failed to create WorldData for world " + worldDataFile.getName() + "!");
                e.printStackTrace();
            }
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(worldDataFile);
        WorldData worldData = new WorldData(worldName);
        configuration.set("name", worldData.getName());
        configuration.set("spawns", "");
        configuration.set("regions", "");

        try {
            configuration.save(worldDataFile);
        } catch (IOException e) {
            getPlugin().getLogger().warning("Failed to save WorldData for world " + worldDataFile.getName() + "!");
            e.printStackTrace();
        }

        return worldData;
    }

    public WorldData loadData(File file) {
        if (!file.exists()) return createData(file.getParentFile().getName(), file);

        System.out.println("LOADING DATA FROM " + file.getAbsolutePath());

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        WorldData worldData = new WorldData(configuration.getString("name"));
        if (!configuration.getString("spawns").isEmpty()) {

            String[] bits = configuration.getString("spawns").split("//");
            for (String bit : bits) {
                String name = bit.substring(0, bit.indexOf(":"));
                SerializedLocation serializedLocation = SerializedLocation.deserialize(bit.substring(bit.indexOf(":") + 1));
                worldData.addSpawn(name, serializedLocation);
            }

        }

        if (!configuration.getString("regions").isEmpty()) {

            String[] bits = configuration.getString("regions").split("//");
            for (String bit : bits) {
                String name = bit.substring(0, bit.indexOf(":"));
                AssiRegion assiRegion = new AssiRegion(bit.substring(bit.indexOf(":") + 1));
                worldData.addRegion(name, assiRegion);
            }

        }

        return worldData;
    }

    public void saveData(WorldData worldData) {
        // D.d("saving " + worldData.getName());
        File worldFile = new File(getWorldDirectory(worldData.getName()), "data.yml");

        if (!worldFile.exists()) {
            try {
                if (!worldFile.createNewFile()) throw new IOException();
            } catch (IOException e) {
                getPlugin().getLogger().severe("Failed to create world file for " + worldData.getName() + "!");
                e.printStackTrace();
            }
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(worldFile);
        yamlConfiguration.set("name", worldData.getName());

        // D.d("spawns " + worldData.getSpawns());

        StringBuilder locationString = new StringBuilder();

        int i = 0;
        for (Map.Entry<String, SerializedLocation> stringSerializedLocationEntry : worldData.getSpawns().entrySet()) {
            String locName = stringSerializedLocationEntry.getKey();
            SerializedLocation location = stringSerializedLocationEntry.getValue();

            if (i > 0) {
                locationString.append("//");
            }

            locationString.append(locName).append(":").append(location.toString());
            i++;
        }

        yamlConfiguration.set("spawns", locationString.toString());

        StringBuilder regionString = new StringBuilder();

        i = 0;
        for (Map.Entry<String, AssiRegion> stringAssiRegionEntry : worldData.getRegions().entrySet()) {
            String regionName = stringAssiRegionEntry.getKey();
            AssiRegion region = stringAssiRegionEntry.getValue();

            if (i > 0) {
                regionString.append("//");
            }

            regionString.append(regionName).append(":").append(region.toString());
            i++;
        }

        yamlConfiguration.set("regions", regionString.toString());

        try {
            yamlConfiguration.save(worldFile);
        } catch (IOException e) {
            getPlugin().getLogger().warning("Failed to save WorldData for world " + worldData.getName() + "!");
            e.printStackTrace();
        }

    }

    private boolean isWorld(File dir) {
        return dir.isDirectory() && new File(dir, "level.dat").exists();
    }

    public File getWorldDirectory(String worldName) {
        return new File(coreDirectory, worldName);
    }

    public File getCoreDirectory() {
        return coreDirectory;
    }

    public WorldData getPrimaryWorld() {
        return getWorldData(Bukkit.getWorlds().get(0).getName());
    }

    /**
     * @return a store of map data.
     * The key contains the world name, the value is its data.
     */
    public Map<String, WorldData> getWorldDataMap() {
        return worldDataMap;
    }

    public WorldData getWorldData(String world) {
        return worldDataMap.get(world);
    }

    public WorldData getWorldData(World world) {
        return getWorldData(world.getName());
    }

}
