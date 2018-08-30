package net.assimilationmc.ellie.assicore.score;

import net.assimilationmc.ellie.assicore.score.assets.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ScoreboardData {

    private ArrayList<AssiScore> assets;

    ScoreboardData(){
        this.assets = new ArrayList<>();
    }

    public ArrayList<String> getLines(ScoreboardManager manager, Player player){

        ArrayList<String> a = new ArrayList<>();

        for(AssiScore assiScore: assets){
            a.addAll(assiScore.getLines(manager, player));
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
        assets.add(new AssiScoreText(line));
    }

    public void writeEmpty(){
        assets.add(new AssiScoreText(" "));
    }

    public void writeRank(){
        assets.add(new AssiScoreRank());
    }

    public void writeCoins(){
        assets.add(new AssiScoreCoins());
    }

    public void writeOnline(){
        assets.add(new AssiScoreOnline());
    }

    public void writeCustom(AssiScore assiScore){
        assets.add(assiScore);
    }

}
