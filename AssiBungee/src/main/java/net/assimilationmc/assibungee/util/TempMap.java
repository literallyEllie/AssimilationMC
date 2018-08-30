package net.assimilationmc.assibungee.util;

import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.AssiBungee;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TempMap<K, V> extends HashMap<K, V> {

    private AssiBungee assiBungee;
    private ExpireTimerPolicy timerType;
    private Map<K, Integer> idMap;

    private int expireAfter;
    private TimeUnit unit;

    public TempMap(AssiBungee assiBungee, ExpireTimerPolicy timerType, int expireAfter, TimeUnit unit) {
        this.assiBungee = assiBungee;
        this.timerType = timerType;
        this.idMap = Maps.newHashMap();

        this.expireAfter = expireAfter;
        this.unit = unit;
    }

    @Override
    public V put(K key, V value) {
        idMap.put(key, assiBungee.getProxy().getScheduler().schedule(assiBungee, () -> this.remove(key), expireAfter, unit).getId());
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        if (idMap.containsKey(key)) {
            assiBungee.getProxy().getScheduler().cancel(idMap.get(key));
            idMap.remove(key);
        }
        return super.remove(key);
    }

    @Override
    public V get(Object key) {
        if (timerType == ExpireTimerPolicy.ACCESS && idMap.containsKey(key)) {
            assiBungee.getProxy().getScheduler().cancel(idMap.get(key));
            idMap.remove(key);
        }
        idMap.put((K) key, assiBungee.getProxy().getScheduler().schedule(assiBungee, () -> this.remove(key), expireAfter, unit).getId());
        return super.get(key);
    }

    public int getExpireAfter() {
        return expireAfter;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public enum ExpireTimerPolicy {

        WRITE,
        ACCESS

    }

}
