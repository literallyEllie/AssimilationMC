package net.assimilationmc.ellie.assiuhc.games.score;

import java.util.ArrayList;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCScoreText extends UHCScore {

    private String line;

    public UHCScoreText(String line) {
        this.line = line;
    }

    @Override
    public ArrayList<String> getLines(){
        ArrayList<String> orderedScores = new ArrayList<>();
        orderedScores.add(line);
        return orderedScores;
    }

}
