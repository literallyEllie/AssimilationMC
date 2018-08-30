package net.assimilationmc.assicore.queue;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.UUID;

public class QueueComparator implements Comparator<Pair<UUID, Long>> {

    @Override
    public int compare(Pair<UUID, Long> o1, Pair<UUID, Long> o2) {
        return o1.getRight().compareTo(o2.getRight());
    }
}
