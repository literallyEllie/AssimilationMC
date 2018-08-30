package net.assimilationmc.assicore.world.waypoint;

import com.google.common.base.Preconditions;
import net.assimilationmc.assicore.util.SerializedLocation;
import net.assimilationmc.assicore.world.WorldData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WayPointReader {

    private final Pattern PATTERN_IN_DROP = Pattern.compile("drop(\\d+)");
    private final Pattern PATTERN_IN_SPAWNS = Pattern.compile("(\\w+ )?(t(\\d+) )?(s(\\d+))");

    private WorldData worldData;
    private File file;

    /**
     * A thing that reads from .points files and
     * transfers them.
     */
    public WayPointReader(WorldData worldData, File from) {
        this.worldData = worldData;
        this.file = from;
        Preconditions.checkNotNull(worldData, "World data cannot be null for the world!");
    }

    public int read() {
        int read = 0;

        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");

            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                if (!line.startsWith("name:")) continue;

                String[] data = line.split(",");

                String waypointName = null;
                int x = 0;
                int y = 0;
                int z = 0;

                pairLoop:
                for (String pair : data) {

                    String key = pair.split(":")[0];
                    String value = pair.split(":")[1];

                    switch (key) {
                        case "name":
                            waypointName = processSpawnName(value);
                            break;
                        case "x":
                            x = Integer.parseInt(value);
                            break;
                        case "y":
                            y = Integer.parseInt(value);
                            break;
                        case "z":
                            z = Integer.parseInt(value);
                            break;
                        default:
                            break pairLoop;
                    }
                }

                if (waypointName == null || waypointName.isEmpty()) continue;

                if (worldData.getSpawns().containsKey(waypointName)) continue;
                worldData.addSpawn(waypointName, new SerializedLocation(worldData.getName(), x, y, z));

                read++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (lineIterator != null)
                LineIterator.closeQuietly(lineIterator);
        }

        return read;
    }

    public File getFile() {
        return file;
    }

    private String processSpawnName(String name) {
        Matcher matcher = PATTERN_IN_DROP.matcher(name);
        if (matcher.find()) {
            return "DP_" + matcher.group(1);
        }

        Matcher spawnMatcher = PATTERN_IN_SPAWNS.matcher(name);
        if (spawnMatcher.find()) {

            String teamId = "", spawnId;

            // team spawn
            if(spawnMatcher.group(2) != null) {
                teamId = "T_" + spawnMatcher.group(3).toUpperCase().trim() + "_";
            }
            spawnId = "S_" + spawnMatcher.group(5).toUpperCase().trim();

            return teamId + spawnId;
        }

        return null;
    }


}
