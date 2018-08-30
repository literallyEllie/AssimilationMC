package com.assimilation.ellie.assicore.task;

import java.util.LinkedList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class MonitorTask implements Runnable {

    private final LinkedList<Double> tpsHistory;

    private final long MAX_TIME = 10 * 1000000;
    private final long TICK_INTERVAL = 50;

    private long lastRun = System.nanoTime();

    public MonitorTask(){
        this.tpsHistory = new LinkedList<>();
        tpsHistory.add(20d);
    }

    @Override
    public void run() {

        final long start = System.nanoTime();
        final long current = System.currentTimeMillis();

        long timeSpent = (start - lastRun) / 1000;

        if(timeSpent == 0){
            timeSpent = 1;
        }

        if(tpsHistory.size() > 10){
            tpsHistory.remove();
        }

        double tps = TICK_INTERVAL * 1000000.0 / timeSpent;

        if(tps <= 21){
            tpsHistory.add(tps);
        }
        lastRun = start;

    }

    public double getAvgTPS(){

        double avg = 0;

        for (Double aDouble : tpsHistory) {
            avg+=aDouble;
        }

        return avg / tpsHistory.size();
    }

}
