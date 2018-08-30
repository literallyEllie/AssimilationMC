package net.assimilationmc.ellie.assicore.event;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.score.AssiScoreboard;
import net.assimilationmc.ellie.assicore.score.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Ellie on 22/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreboardUpdateEvent extends Event {

    public HandlerList handlerList  = new HandlerList();

    private String player;
    private UpdateElement updateElement;

    public ScoreboardUpdateEvent(String player, UpdateElement updateElement){
        this.player = player;
        this.updateElement = updateElement;

        ScoreboardManager scoreboardManager = ModuleManager.getModuleManager().getScoreboardManager();

        if(updateElement.equals(UpdateElement.ONLINE)){
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if(player.equals(player1.getName())) continue;
                if(scoreboardManager.getPlayerScores().get(player1.getName()) != null && Bukkit.getPlayer(player1.getName()) != null){

                    AssiScoreboard scoreboardData = scoreboardManager.getPlayerScores().get(player1.getName());
                    if(scoreboardData.getScoreData().equalsIgnoreCase("default")) {
                        scoreboardData.build(scoreboardManager, player1);
                        scoreboardData.setup(player1);
                    }
                }
            }
            return;
        }

        if(scoreboardManager.getPlayerScores().get(player) != null && Bukkit.getPlayer(player) != null){

            AssiScoreboard scoreboardData = scoreboardManager.getPlayerScores().get(player);

            if(scoreboardData.getScoreData().equalsIgnoreCase("default")) {
                scoreboardData.build(scoreboardManager, Bukkit.getPlayer(player));
                scoreboardData.setup(Bukkit.getPlayer(player));
            }
        }

    }

    public String getPlayer() {
        return player;
    }

    public UpdateElement getUpdateElement() {
        return updateElement;
    }

    public enum UpdateElement {

        RANK, COINS, ONLINE

    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
