package net.assimilationmc.assicore.parkour;

import com.google.common.base.Stopwatch;
import org.bukkit.entity.Player;

public class ParkourPlayer {

    private Player player;
    private long personalBest;
    private Stopwatch stopwatch;
    private int lastCheckpoint;

    public ParkourPlayer(Player player) {
        this.player = player;
        this.stopwatch = Stopwatch.createUnstarted();
    }

    public void start() {
        this.stopwatch.start();
        this.lastCheckpoint = 0;
    }

    public void finish() {
        if (stopwatch.isRunning()) {
            stopwatch.stop();
        }
    }

    public void reset() {
        if (stopwatch.isRunning()) {
            stopwatch.reset();
        }
        start();
    }

    public boolean isRunning() {
        return stopwatch.isRunning();
    }

    public Player getPlayer() {
        return player;
    }

    public long getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(long personalBest) {
        this.personalBest = personalBest;
    }

    public Stopwatch getStopwatch() {
        return stopwatch;
    }

    public int getLastCheckpoint() {
        return lastCheckpoint;
    }

}
