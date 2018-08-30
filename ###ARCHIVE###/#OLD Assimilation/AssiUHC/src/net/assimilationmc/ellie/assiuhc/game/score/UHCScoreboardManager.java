package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Ellie on 16.7.17 for AssimilationMC.
 * Affiliated with www.minevelop.com
 */
public class UHCScoreboardManager {

    private final UHCGame game;
    private final boolean team;
    private HashMap<String, UAssiScoreboard> playerScores;
    private HashMap<String, UScoreboardData> scoreData;

    public UHCScoreboardManager (UHCGame game){
        this.game = game;
        playerScores = new HashMap<>();
        scoreData = new HashMap<>();
        team = game.getTeamManager() != null;
    }

    public void shutdown(){
        playerScores.clear();
        scoreData.clear();
    }

    public UScoreboardData getData(String name, boolean create){
        if(!create)
            return scoreData.get(name);

        if(!scoreData.containsKey(name))
            scoreData.put(name, new UScoreboardData());
        return scoreData.get(name);
    }

    public void createNewScore(Player player, boolean show) {
        playerScores.put(player.getName(), new UAssiScoreboard(this, player, team, game));
        if(show) playerScores.get(player.getName()).build(this, player);
    }

    public HashMap<String, UAssiScoreboard> getPlayerScores() {
        return playerScores;
    }

    public void refreshAll(){
        playerScores.forEach((s, uAssiScoreboard) -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                uAssiScoreboard.refresh(player);
            }
        });
    }

}
