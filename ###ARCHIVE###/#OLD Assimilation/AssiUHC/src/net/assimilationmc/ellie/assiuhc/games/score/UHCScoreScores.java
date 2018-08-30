package net.assimilationmc.ellie.assiuhc.games.score;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCScoreScores extends UHCScore {

    private String key;
    private HashMap<String, Integer> scores;
    private boolean higherBetter;

    public UHCScoreScores(String key, String line, int value, boolean higherBetter) {

        scores = new HashMap<>();
        this.key = key;
        addScore(line, value);
        this.higherBetter = higherBetter;

    }

    @Override
    public ArrayList<String> getLines() {
        ArrayList<String> orderedScores = new ArrayList<>();

        while(orderedScores.size() < scores.size()){
            String bestKey = null;
            int bestScore = 0;

            for (String s : scores.keySet()) {
                if(orderedScores.contains(key)) continue;

                if(bestKey == null || (higherBetter && scores.get(key) >= bestScore) || (!higherBetter && scores.get(key) <= bestScore)){
                    bestKey = key;
                    bestScore = scores.get(key);
                }

            }

           orderedScores.add(bestKey);
        }
        return orderedScores;
    }

    public boolean isKey(String key){
        return this.key.equals(key);
    }

    public void addScore(String line, int value){
        scores.put(line, value);
    }

}
