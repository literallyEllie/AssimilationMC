package net.assimilationmc.ellie.assicore.score;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreboardManager implements Listener {

    private HashMap<String, AssiScoreboard> playerScores;
    private HashMap<String, ScoreboardData> scoreData;


    public ScoreboardManager() {
        playerScores = new HashMap<>();
        this.scoreData = new HashMap<>();
        ScoreboardData data = getData("default", true);

        data.writeEmpty();

        data.write(Util.color(ColorChart.VARIABLE + "Rank"));
        data.writeRank();
        data.writeEmpty();

        data.write(Util.color(ColorChart.VARIABLE + "Coins"));
        data.writeCoins();
        data.writeEmpty();

        data.write(Util.color(ColorChart.VARIABLE + "Links"));
        data.write(Util.color(ColorChart.R + "/web /discord"));
        data.writeEmpty();

        data.write(Util.color(ColorChart.VARIABLE + "Online"));
        data.writeOnline();
        data.writeEmpty();

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        playerScores.remove(e.getPlayer().getName());
    }

    public ScoreboardData getData(String name, boolean create){

        if(!create)
            return scoreData.get(name);

        if(!scoreData.containsKey(name))
            scoreData.put(name, new ScoreboardData());
        return scoreData.get(name);
    }

    public void build(){

        playerScores.keySet().forEach(s -> {

            Player player = Bukkit.getPlayer(s);
            if(player != null){
                playerScores.get(s).build(this, player);
            }else playerScores.remove(s); //avoid clogging
        });
    }

    /**
     * Call on join and any event where they wouldn't have had it previously
     * (i.e UHC Game where they're removed from it during game)
     *
     * @param player The player to give it
     * @param show Should it be shown immediately?
     */
    public void createNewScore(Player player, boolean show){
        playerScores.put(player.getName(), new AssiScoreboard(this, player));
        if(show) playerScores.get(player.getName()).build(this, player);
    }

    public HashMap<String, AssiScoreboard> getPlayerScores() {
        return playerScores;
    }

}
