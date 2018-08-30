package com.assimilation.ellie.assicore.permission;

import com.assimilation.ellie.assicore.manager.ModuleManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 15/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiPermGroup {

    private String name;
    private String prefix;
    private String suffix;
    private Set<String> parents;
    private Set<GroupOption> options;
    private Set<SpigotPermission> permissions;

    public AssiPermGroup(String name, Set<String> parents){
        this.name = name;
        this.prefix = "";
        this.suffix = "";
        this.parents = parents;
        this.options = new HashSet<>();
        this.permissions = new HashSet<>();
        this.permissions = new HashSet<>();
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

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean hasPrefix(){
        return !this.prefix.isEmpty();
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean hasSuffix(){
        return !this.suffix.isEmpty();
    }

    public Set<String> getParents() {
        return parents;
    }

    public boolean hasParents(){
        return !this.parents.isEmpty();
    }

    public Set<GroupOption> getOptions() {
        return options;
    }

    public void setOptions(Set<GroupOption> options) {
        this.options = options;
    }

    public Set<SpigotPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<SpigotPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermissions(){
        return !this.permissions.isEmpty();
    }

    public HashSet<String> calculateAllEffectivePermissions(){
        return calculateEffectivePermissions("*");
    }

    public HashSet<String> calculateEffectivePermissions(String world){
        HashSet<String> perms = new HashSet<>();

        perms.addAll(permissions.stream().filter(spigotPermission -> spigotPermission.onWorld(world)).map(SpigotPermission::getPermission).collect(Collectors.toSet()));


        if(this.hasParents()) {
            ModuleManager.getModuleManager().getPermissionManager().getGroup(this.name).getParents().forEach(s ->
                    perms.addAll(ModuleManager.getModuleManager().getPermissionManager().getGroup(s).calculateEffectivePermissions(world)));
        }
        return perms;
    }

    public boolean isDefault(){
        return this.options.contains(GroupOption.DEFAULT);
    }

}
