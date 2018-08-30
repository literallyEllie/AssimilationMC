package net.assimilationmc.ellie.assiuhc.event;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.score.AssiScoreboard;
import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Ellie on 15.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UScoreboardUpdateEvent extends Event {

    public HandlerList handlerList  = new HandlerList();

    private String player;

    public UScoreboardUpdateEvent(String player){
        this.player = player;

        ScoreboardManager scoreboardManager = ModuleManager.getModuleManager().getScoreboardManager();

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if(player.equals(player1.getName())) continue;
                if(scoreboardManager.getPlayerScores().get(player1.getName()) != null && Bukkit.getPlayer(player1.getName()) != null){

                    AssiScoreboard scoreboardData = scoreboardManager.getPlayerScores().get(player1.getName());
                    if(!scoreboardData.getScoreData().equalsIgnoreCase("default")) {
                        scoreboardData.build(scoreboardManager, player1);
                        scoreboardData.setup(player1);
                    }
                }
            }

    }

    public String getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
