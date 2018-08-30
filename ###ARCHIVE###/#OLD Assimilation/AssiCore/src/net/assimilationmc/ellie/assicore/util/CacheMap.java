package net.assimilationmc.ellie.assicore.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CacheMap<K, V> {

    private final HashMap<K, Integer> taskid;
    private final HashMap<K, V> map;

    private JavaPlugin javaPlugin;
    private int removeAfter;

    public CacheMap(JavaPlugin plugin, int removeAfterSeconds){
        this.map = new HashMap<>();
        this.taskid = new HashMap<>();
        this.removeAfter = removeAfterSeconds;
        this.javaPlugin = plugin;
    }

    private long a(){
        return 20*removeAfter;
    }

    public V put(K key, V value){
        if(get(key) != null)
            return get(key);
        taskid.put(key, Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, () -> remove(key), a()));
        return map.put(key, value);
    }

    public void remove(K key){
        if(map.containsKey(key)){
            if(taskid.containsKey(key)){
                Bukkit.getScheduler().cancelTask(taskid.get(key));
            }
            map.remove(key);
        }
    }

    public V get(K key){
        if(map.containsKey(key)){
            return map.get(key);
        }
        return null;
    }

    public int getRemoveAfterSeconds() {
        return removeAfter;
    }

}
