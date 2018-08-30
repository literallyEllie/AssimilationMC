package com.assimilation.ellie.assibperms.permission;

import com.assimilation.ellie.assibperms.AssiBPerms;

import java.util.*;

/**
 * Created by Ellie on 22/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiPermGroup {

    private String name;
    private String prefix;
    private String suffix;
    private Set<String> parents;
    private Set<GroupOption> options;
    private Set<String> permissions;
    private Set<SpigotPermission> spigotPermissions;

    public AssiPermGroup(String name, Set<String> parents){
        this.name = name;
        this.prefix = "";
        this.suffix = "";
        this.parents = parents;
        this.options = new HashSet<>();
        this.permissions = new HashSet<>();
        this.spigotPermissions = new HashSet<>();
    }

    public AssiPermGroup(String name){
        this(name, new HashSet<>());
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean hasPrefix(){
        return !this.prefix.isEmpty();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean hasSuffix(){
        return !this.suffix.isEmpty();
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Set<String> getParents() {
        return parents;
    }

    public boolean hasParents(){
        return !this.parents.isEmpty();
    }

    public void setParents(Set<String> parents) {
        this.parents = parents;
    }

    public Set<GroupOption> getOptions() {
        return options;
    }

    public void setOptions(Set<GroupOption> options) {
        this.options = options;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean hasPermissions(){
        return !this.permissions.isEmpty();
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<SpigotPermission> getSpigotPermissions() {
        return spigotPermissions;
    }

    public void setSpigotPermissions(Set<SpigotPermission> spigotPermissions) {
        this.spigotPermissions = spigotPermissions;
    }

    public HashSet<String> calculateEffectivePermissions(){
        HashSet<String> perms = new HashSet<>();

        perms.addAll(permissions);

        if(this.hasParents()) {
            AssiBPerms.getAssiBPerms().getGroupManager().getGroup(this.name).getParents().forEach(s ->
                    perms.addAll(AssiBPerms.getAssiBPerms().getGroupManager().getGroup(s).calculateEffectivePermissions()));
        }
        return perms;
    }

    public boolean isDefault(){
        return this.options.contains(GroupOption.DEFAULT);
    }


}
