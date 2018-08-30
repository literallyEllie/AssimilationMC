package net.assimilationmc.assicore.util;

import java.util.Map;
import java.util.Random;

public class UtilRandom {

    public static int randomNumber(int bound) {
        return new Random().nextInt(bound);
    }

    public static <T> T selectWeightedRandom(Map<T, Double> items) {
        // D.d("looking through items");
        double weight = 0d;
        for (Double tWeight : items.values())
            weight += tWeight;
        // D.d("weight done");
        double r = Math.random() * weight;
        double countWeight = 0d;
        for (Map.Entry<T, Double> tDoubleEntry : items.entrySet()) {
           // D.d("item " + tDoubleEntry.getKey());
            countWeight += tDoubleEntry.getValue();
            if (countWeight >= r)
                return tDoubleEntry.getKey();
        }
        // D.d("null");
        return null;
    }

    public static <T> T selectWeightedRandomNew(Map<T, Double> items) {
        double totalWeight = 0d;
        for (Double weight : items.values()) {
            totalWeight += weight;
        }

        double random = Math.random() * totalWeight;

        for (Map.Entry<T, Double> weightedEntry : items.entrySet()) {
            random -= weightedEntry.getValue();
            if (random <= 0d) {
                return weightedEntry.getKey();
            }
        }

        return null;
    }

}
