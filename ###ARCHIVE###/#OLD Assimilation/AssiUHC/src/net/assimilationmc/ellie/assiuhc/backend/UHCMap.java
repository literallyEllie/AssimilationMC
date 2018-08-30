package net.assimilationmc.ellie.assiuhc.backend;

import net.assimilationmc.ellie.assicore.api.AssiRegion;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assiuhc.game.SingledGameType;
import net.assimilationmc.ellie.assiuhc.game.TeamedGameType;
import net.assimilationmc.ellie.assiuhc.game.UHCSpawn;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCMap {

    private final String name;
    private List<String> builders;
    private boolean enabled;

    private int mapSize;
    private AssiRegion region;
    private SerializableLocation lobbySpawn;
    private HashMap<Integer, List<UHCSpawn>> teamSpawns;

    private boolean forTeams;
    private List<SingledGameType> singledGameType;
    private List<TeamedGameType> teamedGameTypes;

    private int selectedSingled;
    private int selectedTeamed;

    private Material material;

    private boolean changed;

    public UHCMap(String name){
        this.name = name;
        this.builders = new ArrayList<>();
        this.teamSpawns = new HashMap<>();
        singledGameType = new ArrayList<>();
        teamedGameTypes = new ArrayList<>();
        this.changed = false;
    }

    public String getName() {
        return name;
    }

    public List<String> getBuilders() {
        return builders;
    }

    public boolean isToggled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SerializableLocation getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(SerializableLocation lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public void setBuilders(List<String> builders) {
        this.builders = builders;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public AssiRegion getRegion() {
        return region;
    }

    public void setRegion(AssiRegion region) {
        this.region = region;
    }

    public HashMap<Integer, List<UHCSpawn>> getTeamSpawns() {
        return teamSpawns;
    }

    public void setTeamSpawns(HashMap<Integer, List<UHCSpawn>> teamSpawns) {
        this.teamSpawns = teamSpawns;
    }

    public boolean isForTeams() {
        return forTeams;
    }

    public void setForTeams(boolean forTeams) {
        this.forTeams = forTeams;
    }

    public List<SingledGameType> getSingledGameType() {
        return singledGameType;
    }

    public List<String> serialisedSingledGameTypes(){
        ArrayList<String> strings = new ArrayList<>();
        singledGameType.forEach(singledGameType -> strings.add(singledGameType.name()));
        return strings;
    }

    public void setSingledGameType(List<SingledGameType> singledGameType) {
        this.singledGameType = singledGameType;
    }

    public List<TeamedGameType> getTeamedGameTypes() {
        return teamedGameTypes;
    }

    public List<String> serialisedTeamedGameTypes(){
        ArrayList<String> strings = new ArrayList<>();
        teamedGameTypes.forEach(teamedGameType -> strings.add(teamedGameType.name()));
        return strings;
    }

    public void setTeamedGameTypes(List<TeamedGameType> teamedGameTypes) {
        this.teamedGameTypes = teamedGameTypes;
    }

    public int getSelectedSingled() {
        return selectedSingled;
    }

    public void setSelectedSingled(int selectedSingled) {
        this.selectedSingled = selectedSingled;
    }

    public int getSelectedTeamed() {
        return selectedTeamed;
    }

    public void setSelectedTeamed(int selectedTeamed) {
        this.selectedTeamed = selectedTeamed;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String toString(){
        return "&6Name: &7"+name+
                " &6Enabled: &7"+enabled+
                " &6For teams: &7"+forTeams+
                " &6Game type: &7"+(forTeams ? getTeamedGameTypes() : getSingledGameType())+
                " &6Map size: &7"+mapSize+
                " &6Builders: &7"+builders+
                " &6Spawns: &7"+teamSpawns.size();
    }

    // - "id###SL:id###SL" (group id)
    public List<String> serialiseSpawns() {
        List<String> spawns = new ArrayList<>();
        for (List<UHCSpawn> spawnList : teamSpawns.values()) {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (UHCSpawn spawn : spawnList) {
                if (i > 0 && i < spawnList.size() && builder.charAt(builder.length()) != ':') {
                    builder.append("###");
                }
                builder.append(spawn.getId()).append("###").append(spawn.getLocation().toString());
                i++;
                if (i != spawnList.size()) {
                    builder.append(":");
                }
            }
            spawns.add(builder.toString());
        }

        return spawns;
    }

}
