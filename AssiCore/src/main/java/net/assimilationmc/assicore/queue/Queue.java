package net.assimilationmc.assicore.queue;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class Queue<K> {

    private Map<K, Set<Pair<UUID, Long>>> queue = new ConcurrentSkipListMap<>();

    /**
     * Get the player in the queue.
     *
     * @param key the key to search for.
     * @return a pair of the player UUID and when they joined the queue
     * or null if they are not in the queue.
     */
    public Pair<UUID, Long> get(K key) {
        if (queue.containsKey(key)) {

            Iterator<Pair<UUID, Long>> iterator = this.queue.get(key).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }

        return null;
    }

    /**
     * Add a player to the queue.
     *
     * @param key    The key of the player to add.
     * @param player A pair of UUID and Long, the uuid of the player and the timestamp of when they joined the queue.
     * @return If they were added.
     */
    public boolean add(K key, Pair<UUID, Long> player) {
        return (!this.queue.containsKey(key) || queue.get(key)
                .stream().noneMatch(p -> p.getLeft().equals(player.getLeft()))) && queue.computeIfAbsent(key, k -> new TreeSet<>(new QueueComparator())).add(player);
    }

    /**
     * Remove a player UUID from the queue.
     *
     * @param uuid The uuid of the player to remove.
     */
    public void remove(UUID uuid) {
        queue.forEach((k, pairs) -> remove(k, uuid));
    }

    /**
     * Remove the player from the queue.
     *
     * @param key  the key of the queue.
     * @param uuid The uuid to add.
     * @return If they were removed from the queued.
     */
    public boolean remove(K key, UUID uuid) {
        if (this.queue.containsKey(key)) {
            Iterator<Pair<UUID, Long>> iterator = this.queue.get(key).iterator();
            while (iterator.hasNext()) {
                Pair<UUID, Long> p = iterator.next();
                if (p.getLeft().equals(uuid)) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the queue contains someone.
     *
     * @param uuid the uuid of the player to check.
     * @param k    The key to query.
     * @return If they were removed from the queue.
     */
    public boolean contains(UUID uuid, K k) {
        return k != null && queue.containsKey(k) && queue.get(k).stream().filter(uuidLongPair -> uuidLongPair.getLeft().equals(uuid)).findAny().orElse(null) != null;
    }

    /**
     * @return the queue.
     */
    public Map<K, Set<Pair<UUID, Long>>> getQueue() {
        return queue;
    }

}
