package net.assimilationmc.ellie.assiuhc.game;

import net.assimilationmc.ellie.assicore.util.Util;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum GameState {

    WAITING, WARMUP, INGAME, FINISHED;

    public String toString(){
        switch (this){

            case WAITING:
                return Util.color("&aWaiting");
            case WARMUP:
                return Util.color("&cWarmup");
            case INGAME:
                return Util.color("&cIn-Game");
            case FINISHED:
                return Util.color("&7Finished");

        }
        return name();
    }

}
