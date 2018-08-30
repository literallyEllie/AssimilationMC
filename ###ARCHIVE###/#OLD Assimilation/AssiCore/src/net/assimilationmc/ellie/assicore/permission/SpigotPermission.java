package net.assimilationmc.ellie.assicore.permission;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SpigotPermission {

    private String permission;
    private Set<String> worlds;

    public SpigotPermission(String permission) {
        this.permission = permission;
        this.worlds = new HashSet<>();
    }

    public String getPermission() {
        return permission;
    }

    public Set<String> getWorlds() {
        return worlds;
    }

    public void setWorlds(Set<String> worlds) {
        this.worlds = worlds;
    }

    public void addWorld(String world) {
        worlds.add(world);
    }

    public boolean onWorld(String world) {
        return worlds.contains("*") || worlds.contains(world);
    }

}
