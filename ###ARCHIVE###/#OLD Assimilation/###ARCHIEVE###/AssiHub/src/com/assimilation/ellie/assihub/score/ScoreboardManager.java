package com.assimilation.ellie.assihub.score;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreboardManager implements Listener {

    private HashMap<String, AssiScoreboard> playerScores;
    private HashMap<String, ScoreboardData> scoreData;


    public ScoreboardManager(){
        playerScores = new HashMap<>();
        this.scoreData = new HashMap<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        playerScores.put(e.getPlayer().getName(), new AssiScoreboard(this, e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        playerScores.remove(e.getPlayer().getName());
    }

    public ScoreboardData getData(String navn, boolean create){

        if(!create)
            return scoreData.get(navn);

        if(!scoreData.containsKey(navn))
            scoreData.put(navn, new ScoreboardData());
        return scoreData.get(navn);
    }

    public void build(){

        playerScores.keySet().forEach(s -> {

            Player player = Bukkit.getPlayer(s);
            if(player != null){
                playerScores.get(s).build(this, player);
            }
        });
    }


}
