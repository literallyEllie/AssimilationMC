package net.assimilationmc.ellie.assicore.permission;

import com.google.gson.internal.LinkedTreeMap;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 15/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiPermGroup {

    private String group_name;
    private String prefix;
    private String suffix;
    private List<String> parents = new ArrayList<>();
    private List<GroupOption> options = new ArrayList<>();
    private List<SpigotPermission> permissions = new ArrayList<>();

    public AssiPermGroup(){
    }

    public AssiPermGroup(String name, List<String> parents){
        this.group_name = name;
        this.prefix = "";
        this.suffix = "";
        this.parents = parents;
        this.options = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    public AssiPermGroup(String name){
        this(name, new ArrayList<>());
    }

    public String getName() {
        return group_name;
    }

    public void setGroupName(String name) {
        this.group_name = name;
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

    public List<String> getParents() {
        return parents;
    }

    public void setParents(String parents) {
        this.parents = JsonUtil.from_gson(parents);
    }

    public boolean hasParents(){
        return !this.parents.isEmpty();
    }

    public List<GroupOption> getOptions() {
        return options;
    }

    public void setOptions(String options) {
        List<String> a = JsonUtil.from_gson(options);
        List<GroupOption> optionList = new ArrayList<>();
        a.forEach(s -> optionList.add(GroupOption.valueOf(s)));
        this.options = optionList;
    }

    public List<SpigotPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        ArrayList<LinkedTreeMap<String, Object>> a = JsonUtil.from_gson(permissions);
        List<SpigotPermission> permissionList = new ArrayList<>();
        a.forEach(s -> {
            //try {
                SpigotPermission permission = new SpigotPermission((String)s.get("permission"));
                ArrayList<String> worlds = (ArrayList<String>)s.get("worlds");
                for (Object o : worlds) {
                   permission.addWorld((String)o);
                }
                permissionList.add(permission);
        });
        this.permissions = permissionList;
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
            ModuleManager.getModuleManager().getPermissionManager().getGroup(this.group_name).getParents().forEach(s ->
                    perms.addAll(ModuleManager.getModuleManager().getPermissionManager().getGroup(s).calculateEffectivePermissions(world)));
        }
        return perms;
    }

    public boolean isDefault() {
        return this.options.contains(GroupOption.DEFAULT);
    }

}
