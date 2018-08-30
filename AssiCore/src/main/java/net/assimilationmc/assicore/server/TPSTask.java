package net.assimilationmc.assicore.server;

import com.google.common.collect.Lists;

import java.util.LinkedList;

public class TPSTask implements Runnable {

    private final LinkedList<Double> tpsHistory;
    private long last;

    public TPSTask() {
        this.last = System.nanoTime();
        this.tpsHistory = Lists.newLinkedList();
    }

    @Override
    public void run() {
        final long start = System.nanoTime();

        long timeSpent = (start - last) / 1000;

        if (timeSpent == 0) {
            timeSpent = 1;
        }

        if (tpsHistory.size() > 10) {
            tpsHistory.remove();
        }

        double tps = 50 * 1000000.0 / timeSpent;
        if (tps <= 21)
            tpsHistory.add(tps);
        last = start;
    }

    public double getAverage() {
        double average = 0d;

        for (Double aDouble : tpsHistory) {
            if (aDouble != null)
                average += aDouble;
        }
        return average / tpsHistory.size();
    }

}
