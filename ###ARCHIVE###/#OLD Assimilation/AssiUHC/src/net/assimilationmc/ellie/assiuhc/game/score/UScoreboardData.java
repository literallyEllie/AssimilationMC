package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.score.asset.ScoreUText;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 16.7.17 for votifier.
 * Affiliated with www.minevelop.com
 */
public class UScoreboardData {

    private ArrayList<UAssiScore> assets;

    public UScoreboardData(){
        this.assets = new ArrayList<>();
    }

    public ArrayList<String> getLines(UHCScoreboardManager manager, Player player, UHCGame game){

        ArrayList<String> a = new ArrayList<>();

        for(UAssiScore assiScore: assets){
            a.addAll(assiScore.getLines(manager, player, game));
        }

        return a;
    }

    public void clear(){
        assets.clear();
    }

    String prepareLine(String line){

        if(line.length() > 28){

            String l1 = line.substring(0, 16);
            String c = ChatColor.getLastColors(l1);
            String l2 = line.substring(16);

            int length = 16 - (c + l2).length();

            if(length > 0){
                return l1 + l2.substring(0, l2.length() - length);
            }
        }
        return line;
    }

    public void write(String line){
        line = prepareLine(line);
        assets.add(new ScoreUText(line));
    }

    public void writeEmpty(){
        assets.add(new ScoreUText(" "));
    }

    public void writeScore(UAssiScore assiScore){
        assets.add(assiScore);
    }


}
