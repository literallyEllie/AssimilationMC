package net.assimilationmc.ellie.assiuhc.backend;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.assimilationmc.ellie.assicore.api.AssiRegion;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assicore.util.FileUtil;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.SingledGameType;
import net.assimilationmc.ellie.assiuhc.game.TeamedGameType;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.UHCSpawn;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class MapManager {

    private UHC uhc;
    private File dir;

    private HashMap<String, UHCMap> maps;
    private HashMap<String, UHCMap> construction;

    public MapManager(UHC uhc){
        this.uhc = uhc;
        this.dir = new File(uhc.getDataFolder(), "maps");
        if(!dir.exists()) FileUtil.createDirectory(dir);
        this.construction = new HashMap<>();
        loadMaps();
    }

    public void finish(){

        maps.forEach((s, map) -> {

            if(map.isChanged()) {
                saveMap(map, false);
            }

        });
        maps.clear();

        construction.forEach((s, map) -> saveMap(map, true));
        construction.clear();
        uhc = null;
        dir = null;

    }

    private void loadMaps() {
        maps = new HashMap<>();

        int i = 0;
        for (File file : dir.listFiles()) {

            if (file.getName().contains("construction_")) {

                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                String name = cfg.getString("name");
                UHCMap uhcMap = new UHCMap(name);
                AssiRegion region = new AssiRegion(cfg.getString("region"));
                uhcMap.setRegion(region);

                int mapSize = cfg.getInt("mapSize");
                uhcMap.setMapSize(mapSize);

                uhcMap.setEnabled(cfg.getBoolean("enabled"));

                if (cfg.get("lobby") != null) {
                    SerializableLocation serializableLocation = new SerializableLocation(cfg.getString("lobby"));
                    uhcMap.setLobbySpawn(serializableLocation);
                }

                List<String> builders = cfg.getStringList("builders");
                if (builders != null) {
                    uhcMap.setBuilders(builders);
                }

                // spawns:
                // - "id###SL:id###SL" (group id)
                List<String> serialisedSpawns = cfg.getStringList("spawns");
                HashMap<Integer, List<UHCSpawn>> spawns = new HashMap<>();
                if (serialisedSpawns != null) {
                    int y = 0;
                    for (String serialisedSpawn : serialisedSpawns) { // for list returns 'id###SL:id###SL'

                        String[] splitSpawns = serialisedSpawn.split(":"); // returns 'id###SL'
                        List<UHCSpawn> spawnList = new ArrayList<>();
                        for (String splitSpawn : splitSpawns) {
                            String[] verySplitSpawn = splitSpawn.split("###"); // returns Id  SL
                            int id = Integer.parseInt(verySplitSpawn[0]); // id
                            SerializableLocation serializableLocation = new SerializableLocation(verySplitSpawn[1]); // SL
                            UHCSpawn spawn = new UHCSpawn(serializableLocation);
                            spawn.setId(id);
                            spawnList.add(spawn);
                        }
                        spawns.put(y, spawnList);
                        y++;
                    }
                }
                uhcMap.setTeamSpawns(spawns);

                boolean forTeams = cfg.getBoolean("forTeams"); // single // double
                uhcMap.setForTeams(forTeams);

                try {
                    if (forTeams) {
                        List<TeamedGameType> teamedGameTypes = new ArrayList<>();
                        for (String s : cfg.getStringList("gameType")) {
                            teamedGameTypes.add(TeamedGameType.valueOf(s.toUpperCase()));
                        }
                        uhcMap.setTeamedGameTypes(teamedGameTypes);
                    } else {
                        List<SingledGameType> singledGameTypes = new ArrayList<>();
                        for (String s : cfg.getStringList("gameType")) {
                            singledGameTypes.add(SingledGameType.valueOf(s.toUpperCase()));
                        }
                        uhcMap.setSingledGameType(singledGameTypes);
                    }
                } catch (Exception e) {
                    uhc.logW("Gametypes invalid for map " + name + " " + e.getMessage());
                }

                if (cfg.getString("displayItem") != null) {
                    uhcMap.setMaterial(Material.valueOf(cfg.getString("displayName")));
                }

                this.construction.put(name.toLowerCase(), uhcMap);

            } else {
                try {
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                    String name = cfg.getString("name");
                    UHCMap uhcMap = new UHCMap(name);
                    AssiRegion region = new AssiRegion(cfg.getString("region"));
                    uhcMap.setRegion(region);

                    int mapSize = cfg.getInt("mapSize");
                    uhcMap.setMapSize(mapSize);

                    uhcMap.setEnabled(cfg.getBoolean("enabled"));

                    SerializableLocation serializableLocation = new SerializableLocation(cfg.getString("lobby"));
                    uhcMap.setLobbySpawn(serializableLocation);

                    List<String> builders = cfg.getStringList("builders");
                    uhcMap.setBuilders(builders);

                    boolean forTeams = cfg.getBoolean("forTeams"); // single // double
                    uhcMap.setForTeams(forTeams);

                    List<String> serialisedSpawns = cfg.getStringList("spawns");
                    HashMap<Integer, List<UHCSpawn>> spawns = new HashMap<>();
                    int y = 0;
                    for (String serialisedSpawn : serialisedSpawns) { // for list returns 'id###SL:id###SL'

                        String[] splitSpawns = serialisedSpawn.split(":"); // returns 'id###SL'
                        List<UHCSpawn> spawnList = new ArrayList<>();
                        for (String splitSpawn : splitSpawns) {
                            String[] verySplitSpawn = splitSpawn.split("###"); // returns Id  SL
                            int id = Integer.parseInt(verySplitSpawn[0]); // id
                            SerializableLocation serializableLocation1 = new SerializableLocation(verySplitSpawn[1]); // SL
                            UHCSpawn spawn = new UHCSpawn(serializableLocation1);
                            spawn.setId(id);
                            spawnList.add(spawn);
                        }
                        spawns.put(y, spawnList);
                        y++;
                    }
                    uhcMap.setTeamSpawns(spawns);

                    if (forTeams) {
                        List<TeamedGameType> teamedGameTypes = new ArrayList<>();
                        for (String s : cfg.getStringList("gameType")) {
                            teamedGameTypes.add(TeamedGameType.valueOf(s.toUpperCase()));
                        }
                        uhcMap.setTeamedGameTypes(teamedGameTypes);
                    } else {
                        List<SingledGameType> singledGameTypes = new ArrayList<>();
                        for (String s : cfg.getStringList("gameType")) {
                            singledGameTypes.add(SingledGameType.valueOf(s.toUpperCase()));
                        }
                        uhcMap.setSingledGameType(singledGameTypes);
                    }

                    try {
                        uhcMap.setMaterial(Material.valueOf(cfg.getString("displayItem")));
                    } catch (IllegalArgumentException e) {
                        uhc.logE("Failed to set material!");
                    }

                    this.maps.put(name.toLowerCase(), uhcMap);
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    uhc.logW("Invalid map at " + file.getName());
                }
            }
        }
        uhc.logI("Loaded " + i + " maps.");
    }

    public void saveMap(UHCMap map, boolean construction) {

        if (!construction) {
            if (getActiveMapFile(map.getName()) == null) {
                FileUtil.createFile(new File(dir, map.getName()+".yml"));
            }

            File f = getActiveMapFile(map.getName());
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

            cfg.set("name", map.getName());
            cfg.set("region", map.getRegion().toString());
            cfg.set("mapSize", map.getMapSize());
            cfg.set("lobby", map.getLobbySpawn().toString());
            cfg.set("builders", map.getBuilders());
            cfg.set("forTeams", map.isForTeams());
            cfg.set("gameType", map.isForTeams() ? map.serialisedTeamedGameTypes() : map.serialisedSingledGameTypes());
            cfg.set("displayItem", map.getMaterial().name());
            cfg.set("enabled", map.isToggled());
            cfg.set("spawns", map.serialiseSpawns());

            FileUtil.saveFile(cfg, f);

            return;
        }

        if (getConstructionMapFile(map.getName()) == null) {
            FileUtil.createFile(new File(dir, "construction_"+map.getName()+".yml"));
        }
        File f = getConstructionMapFile(map.getName());
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        cfg.set("name", map.getName());
        if(map.getRegion() !=null)
            cfg.set("region", map.getRegion().toString());
        if(map.getMapSize() != 0)
            cfg.set("mapSize", map.getMapSize());
        if(map.getLobbySpawn() != null)
            cfg.set("lobby", map.getLobbySpawn().toString());
        if(!map.getBuilders().isEmpty())
            cfg.set("builders", map.getBuilders());
        cfg.set("forTeams", map.isForTeams());
        if(!(map.getTeamedGameTypes().isEmpty() && map.getSingledGameType().isEmpty()))
            cfg.set("gameType", map.isForTeams() ? map.serialisedTeamedGameTypes() : map.serialisedSingledGameTypes());
        if(map.getMaterial() != null)
            cfg.set("displayItem", map.getMaterial().name());
        cfg.set("enabled", map.isToggled());
        FileUtil.saveFile(cfg, f);

    }

    public void createMap(String name, AssiRegion region){
        if(getMap(name) == null && construction.get(name) == null) {

            UHCMap map = new UHCMap(name);
            map.setRegion(region);
            this.construction.put(name.toLowerCase(), map);
        }
    }

    public void setBuilders(String name, List<String> builders, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.setBuilders(builders);
            map.setChanged(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public void addTeamedGameType(String name, TeamedGameType gameTypes, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.getTeamedGameTypes().add(gameTypes);
            map.setChanged(true);
            map.setForTeams(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public void addSingledGameType(String name, SingledGameType gameTypes, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.getSingledGameType().add(gameTypes);
            map.setChanged(true);
            map.setForTeams(false);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public void setMapSize(String name, int mapSize, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.setMapSize(mapSize);
            map.setChanged(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public void setTeamSpawns(String name, HashMap<Integer, List<UHCSpawn>> spawns, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.setTeamSpawns(spawns);
            map.setChanged(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    @Deprecated
    public void setType(String name, String type, boolean forTeams, boolean setup){
        if(forTeams){
            try {
                TeamedGameType gameTypes = TeamedGameType.valueOf(type.toUpperCase());
                if(setup){
                    getConstructionMap(name).getTeamedGameTypes().add(gameTypes);
                    getConstructionMap(name).setForTeams(true);
                    getConstructionMap(name).setChanged(true);
                }else{
                    getMap(name).getTeamedGameTypes().add(gameTypes);
                    getMap(name).setForTeams(true);
                    getMap(name).setChanged(true);
                }
            }catch(IllegalArgumentException e){
            }
        }else{
            try {
                SingledGameType gameTypes = SingledGameType.valueOf(type.toUpperCase());
                if(setup){
                    getConstructionMap(name).getSingledGameType().add(gameTypes);
                    getConstructionMap(name).setForTeams(false);
                    getConstructionMap(name).setChanged(true);
                }else{
                    getMap(name).getSingledGameType().add(gameTypes);
                    getMap(name).setForTeams(false);
                    getMap(name).setChanged(true);
                }
            }catch(IllegalArgumentException e){
            }
        }
    }

    public void setLobby(String name, SerializableLocation location, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.setLobbySpawn(location);
            map.setChanged(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public void setDisplayMaterial(String name, Material material, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.setMaterial(material);
            map.setChanged(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public void setToggled(String name, boolean toggled, boolean setup){
        UHCMap map = setup ? getConstructionMap(name) : getMap(name);
        if(map != null) {
            map.setEnabled(toggled);
            map.setChanged(true);
            if (setup) {
                construction.put(name.toLowerCase(), map);
            }else maps.put(name.toLowerCase(), map);
        }
    }

    public boolean finishConstructionMap(String name){
        if(getConstructionMap(name) != null){

            UHCMap map = getConstructionMap(name);
            if(validate(map)){
                maps.put(name.toLowerCase(), getConstructionMap(name));
                saveMap(map, false);
                construction.remove(name.toLowerCase());
                FileUtil.deleteFile(getConstructionMapFile(name));
                return true;
            }
            throw new IllegalArgumentException("Map isn't valid");
        }
        return false;
    }

    public UHCMap getConstructionMap(String name){
        return construction.get(name.toLowerCase());
    }

    public UHCMap getMap(String name){
        return maps.get(name.toLowerCase());
    }

    public File getDirectory() {
        return dir;
    }

    private File getActiveMapFile(String name){
        for (File file : dir.listFiles()) {
            if(file.getName().replace(".yml", "").equalsIgnoreCase(name))
                return file;
        }
        return null;
    }

    private File getConstructionMapFile(String name){
        for (File file : dir.listFiles()) {
            if(file.getName().contains("construction_")){
                if(file.getName().replace(".yml", "").equalsIgnoreCase("construction_"+name))
                    return file;
            }
        }
        return null;
    }

    private boolean validate(UHCMap map){
        return (map.getMapSize() != -1 && map.getBuilders().size() != 0 && (map.isForTeams()
                ? map.getTeamedGameTypes() : map.getSingledGameType()) != null && map.getRegion() != null && map.getLobbySpawn() != null);
    }

    public HashMap<String, UHCMap> getMaps() {
        return maps;
    }

    public HashMap<String, UHCMap> getConstruction() {
        return construction;
    }

    public void preStartGame(UHCMap map, int id) {
        MVWorldManager worldManager = getMV().getMVWorldManager();
        if (!worldManager.cloneWorld("uhc_preset_" + map.getName(), "uhc_" + map.getName() + id)) {
            uhc.logW("Failed to dupe world for " + map.getName() + " " + id);
            return;
        }
        MultiverseWorld world = worldManager.getMVWorld("uhc_" + map.getName() + id);
        world.setAdjustSpawn(false);
        world.setAutoHeal(false);
        world.setBedRespawn(false);
        world.setGameMode(GameMode.SURVIVAL);
        world.setAllowMonsterSpawn(false);
        world.setPVPMode(true);
        world.setAutoLoad(false);
        world.setDifficulty(Difficulty.HARD);
        world.setEnableWeather(false);

        if (!map.isForTeams() && map.getSingledGameType().get(map.getSelectedSingled()) == SingledGameType.ULTRA_ULTRA_HARDCORE) {
            world.setAllowMonsterSpawn(true);
        }

    }

    public void postEndGame(UHCMap map, int id) {
        MVWorldManager worldManager = getMV().getMVWorldManager();
        worldManager.deleteWorld("uhc_" + map.getName() + id, true, true);
    }

    public MultiverseWorld getWorld(UHCGame game){
        return getMV().getMVWorldManager().getMVWorld("uhc_"+game.getMap().getName()+game.getId());
    }

    public MultiverseCore getMV(){
        return MultiverseCore.getPlugin(MultiverseCore.class);
    }

}
