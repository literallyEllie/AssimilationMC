package com.assimilation.ellie.assicore.permission;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SpigotPermission {

    private String permission;
    private Set<String> servers;
    private Set<String> worlds;

    public SpigotPermission(String permission){
        this.permission = permission;
        this.servers = new HashSet<>();
        this.worlds = new HashSet<>();
    }

    public String getPermission() {
        return permission;
    }

    public Set<String> getServers() {
        return servers;
    }

    public void setServers(Set<String> servers) {
        this.servers = servers;
    }

    public Set<String> getWorlds() {
        return worlds;
    }

    public void setWorlds(Set<String> worlds) {
        this.worlds = worlds;
    }

    public boolean onServer(String server){
        return servers.contains("*") || servers.contains(server);
    }

    public boolean onWorld(String world){
        return worlds.contains("*") || worlds.contains(world);
    }

}
