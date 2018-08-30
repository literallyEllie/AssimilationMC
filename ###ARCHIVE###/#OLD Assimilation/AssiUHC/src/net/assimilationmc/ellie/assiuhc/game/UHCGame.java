package net.assimilationmc.ellie.assiuhc.game;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.games.GameScoreboard;
import net.assimilationmc.ellie.assiuhc.games.task.GameKeeperTask;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCGame {

    private int id;
    private UHCMap map;
    private GameState gameState;
    private TeamManager teamManager;
    private List<String> players;
    private List<String> spectators;
    private GameScoreboard scoreboard;
    private GameKeeperTask gameKeeperTask;
    private UHCSpawnManager uhcSpawnManager;

    // task

    public UHCGame(){
    }

    public UHCGame(UHCMap map){
        this.map = map;
        this.gameState = GameState.WAITING;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        if(map.isForTeams()) {
            this.teamManager = new TeamManager(this);
        }
        scoreboard = new GameScoreboard(this);
        this.scoreboard.createTeams();
        this.gameKeeperTask = new GameKeeperTask(this);
        this.uhcSpawnManager = new UHCSpawnManager(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public UHCMap getMap() {
        return map;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public List<String> getPlayers() {
        return players;
    }

    public List<String> getSpectators() {
        return spectators;
    }

    public GameScoreboard getScoreboard() {
        return scoreboard;
    }

    public void addSpectator(Player player, boolean sendLobby){

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        if(sendLobby) player.teleport(map.getLobbySpawn().toLocation());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.3f);
        player.setWalkSpeed(0.3f);
        player.setGameMode(GameMode.SPECTATOR);
        //((CraftPlayer) player).getHandle().k = false;

        scoreboard.setTeam(player, "spec");
        spectators.add(player.getName());
    }

    public MultiverseWorld getDedicatedWorld(){
        return UHC.getPlugin(UHC.class).getMapManager().getWorld(this);
    }

    public boolean isSpectator(Player player){
        return spectators.contains(player.getName());
    }

    public GameKeeperTask getGameKeeperTask() {
        return gameKeeperTask;
    }

    public UHCSpawnManager getUhcSpawnManager() {
        return uhcSpawnManager;
    }

}
