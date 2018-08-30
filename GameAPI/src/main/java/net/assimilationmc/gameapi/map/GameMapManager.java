package net.assimilationmc.gameapi.map;

import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

public class GameMapManager extends GameModule {

    private WorldData selectedWorld;
    private File mapDirectory;

    public GameMapManager(AssiGame assiGame) {
        super(assiGame, "Map Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
        String map = System.getProperty("map");
        if (new File(map).isDirectory()) {
            loadInWorld(new File(map));
            return;
        }

        this.mapDirectory = new File("maps");
        if (!mapDirectory.isDirectory()) {
            mapDirectory.mkdirs();
            log(Level.WARNING, "");
            log(Level.WARNING, "MAP DIRECTORY CREATED - PLEASE POPULATE FOR API TO FUNCTION CORRECTLY");
            log(Level.WARNING, "");
        }

        File directoryPossible;
        if (getAssiGame().getAssiGameMeta().getSubType() == null) {
            directoryPossible = new File(mapDirectory, getAssiGame().getAssiGameMeta().getId());
            if (!directoryPossible.isDirectory()) {
                directoryPossible.mkdirs();
                log(Level.WARNING, "");
                log(Level.WARNING, "GAME TYPE DIRECTORY \"" + directoryPossible.getName() + "\" WAS JUST CREATED, PLEASE POPULATE FOR API TO FUNCTION CORRECTLY.");
                log(Level.WARNING, "");
            }
        } else {
            directoryPossible = new File(mapDirectory, getAssiGame().getAssiGameMeta().getSubType());
            if (!directoryPossible.isDirectory()) {
                directoryPossible.mkdirs();
            }
        }

        String selectedMap = System.getProperty("map");
        if (selectedMap == null && directoryPossible.listFiles().length > 1) {
            selectRandomWorld(directoryPossible);
            return;
        }

        if (selectedMap != null) {
            File file = new File(directoryPossible, selectedMap);
            if (!file.isDirectory()) {
                log("Selected map " + selectedMap + " does not exist!");
            } else {
                loadInWorld(file);
            }
        } else if (directoryPossible.listFiles().length == 1) {
            File file = directoryPossible.listFiles()[0];
            loadInWorld(file);
        }

        if (selectedWorld == null) {
            log(Level.WARNING, "There is no selected map!");
        }

        D.d("World selected " + selectedWorld.getName());
    }

    @Override
    public void end() {

    }

    public void selectRandomWorld(File mapDirectory) {
        int i = new Random().nextInt(mapDirectory.listFiles().length);
        File directory = mapDirectory.listFiles()[i];

        log("From a random selection, the Game Map will be " + directory.getName());
        loadInWorld(directory);
    }

    public void loadInWorld(File file) {
        File target = new File(file.getName());
        if (!file.getAbsoluteFile().equals(target.getAbsoluteFile())) {
            if (target.isDirectory()) {
                target.delete();
                Bukkit.unloadWorld(target.getName(), false);
            }

            log("Loading game map from " + file.getAbsolutePath() + " to " + target.getAbsolutePath() + "...");

            try {
                FileUtils.copyDirectory(file, target);
            } catch (IOException e) {
                log(Level.WARNING, "Failed to copy directory " + file.getName() + " to " + target.getName() + "!");
                e.printStackTrace();
                return;
            }
        }

        Bukkit.createWorld(new WorldCreator(target.getName()));
        log("Game World loaded!");

        selectedWorld = getAssiGame().getPlugin().getWorldManager().getWorldData(target.getName());
    }

    private File allPossibleMaps() {
        return new File(mapDirectory, getAssiGame().getAssiGameMeta().getId());
    }

    public WorldData getSelectedWorld() {
        return selectedWorld;
    }

}
