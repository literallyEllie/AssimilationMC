package net.assimilationmc.ellie.assiuhc.game;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ellie on 11/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCSpawnManager {

    private HashMap<Integer, List<UHCSpawn>> spawns; // group id / spawns
    private HashMap<String, Integer> teams; // team / group id
    private LinkedHashMap<Integer, Integer> nextSpawn;

    private boolean teamsEnabled;
    private MultiverseWorld world;

    private int singleCount;

    public UHCSpawnManager(UHCGame uhcGame){
        this.spawns = uhcGame.getMap().getTeamSpawns();
        this.teamsEnabled = uhcGame.getTeamManager() != null;
        this.teams = new HashMap<>();
        this.nextSpawn = new LinkedHashMap<>();
    }

    public void ready(UHCGame game){
        this.world = game.getDedicatedWorld();
        if(teamsEnabled){
            int i = 0;
            for (Map.Entry<String, UHCTeam> stringUHCTeamEntry : game.getTeamManager().getTeams().entrySet()) {
                teams.put(stringUHCTeamEntry.getKey().toLowerCase(), i);
                nextSpawn.put(i, 0);
                i++;
            }

        }else singleCount = 0;
    }

    public void teleport(String team, Player player) {
        if (teamsEnabled && team != null) {
            if (teams.containsKey(team.toLowerCase())) {
                int id = teams.get(team.toLowerCase()); // group id from teams
                int next = nextSpawn.get(id); // the next spawn
                final SerializableLocation location = spawns.get(id).get(next).getLocation();
                location.setWorld(world.getName());
                player.teleport(location.toLocation());
                nextSpawn(id);
            }
            return;
        }

        List<UHCSpawn> sp = spawns.get(singleCount);
        if (sp == null) {
            singleCount = 0;
            sp = spawns.get(singleCount);
        }

        final SerializableLocation location = sp.get(0).getLocation();
        location.setWorld(world.getName());
        player.teleport(location.toLocation());
        this.singleCount = this.singleCount + 1;
    }

    private void nextSpawn(int group){
        final int next = Math.addExact(nextSpawn.get(group), 1);
        nextSpawn.remove(group);
        nextSpawn.put(group, next);
    }

}
