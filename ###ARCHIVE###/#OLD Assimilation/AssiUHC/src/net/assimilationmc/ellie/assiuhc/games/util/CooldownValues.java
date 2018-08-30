package net.assimilationmc.ellie.assiuhc.games.util;

import net.assimilationmc.ellie.assicore.util.UtilTime;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum CooldownValues {

    FIRST(1, "30m"),
    SECOND(2, "45m"),
    THIRD(3, "1hr"),
    FORTH(4, "1hr 30m"),
    FIFTH(5, "3hr"),
    SIXTH(6, "12hr"),
    SEVENTH(7, "2d"),
    EIGHTH(8, "3d"),
    NINTH(9, "3d 12hr"),
    TENTH(10, "7d"),
    ELEVENTH(11, "1month")

    //KEEP FOR RANKED
    ;

    private int strike;
    private String length;


    CooldownValues(int strike, String length) {
        this.strike = strike;
        this.length = length;
    }

    public static CooldownValues byStrike(int strike){
        for(CooldownValues cooldownValues: CooldownValues.values()){
            if(cooldownValues.getStrike() == strike)
                return cooldownValues;
        }
        return null;
    }

    public int getStrike() {
        return strike;
    }

    public String getLength() {
        return length;
    }

    public long getFutureCooldown(){
        return UtilTime.parseDuration(length);
    }



}
