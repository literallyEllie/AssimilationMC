package net.assimilationmc.ellie.assiuhc.game;

import org.bukkit.World;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameBoarder {

    private World world;
    private int size;

    public GameBoarder(int size, World world) {
        this.world = world;
        this.size = size;

        world.getWorldBorder().setSize(size);
        world.getWorldBorder().setWarningDistance(10);
        world.getWorldBorder().setDamageAmount(10);
        world.getWorldBorder().setWarningTime(3);
        world.getWorldBorder().setDamageBuffer(0);
    }

    public void start(int seconds){
        world.getWorldBorder().setSize(world.getWorldBorder().getSize(), seconds);
    }

    public void stop(){
        world.getWorldBorder().setSize(world.getWorldBorder().getSize());
    }

}
