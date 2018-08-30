package com.assimilation.ellie.assicore.manager;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.permission.AssiPermGroup;
import com.assimilation.ellie.assicore.task.GroupSyncTask;
import com.assimilation.ellie.assicore.util.SQLQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PermissionManager implements IManager {

    private HashMap<String, AssiPermGroup> groups;
    private HashMap<UUID, PermissionAttachment> permissionAttachment;

    private GroupSyncTask groupSyncTask;
    private int task;

    @Override
    public boolean load() {

        groups = new HashMap<>();
        permissionAttachment = new HashMap<>();

        groupSyncTask = new GroupSyncTask(this);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(AssiCore.getCore().getAssiPlugin(), groupSyncTask, 10L, 12000L);

        Bukkit.getOnlinePlayers().forEach(o -> playerJoin(o));

        return true;
    }

    @Override
    public boolean unload() {

        if(groupSyncTask != null){
            Bukkit.getScheduler().cancelTask(task);
            groupSyncTask = null;
            task = -1;
        }

        groups.clear();
        Bukkit.getOnlinePlayers().forEach(o -> o.removeAttachment(permissionAttachment.get(o.getUniqueId())));
        permissionAttachment.clear();


        return true;
    }

    @Override
    public String getModuleID() {
        return "permission";
    }

    public HashMap<String, AssiPermGroup> getGroups() {
        return groups;
    }

    public AssiPermGroup getGroup(String id){
        return groups.get(id.toLowerCase());
    }

    public Set<String> calculateDifference(AssiPermGroup permGroup, Player player, String world){
        //                             if the player doesn't have the permissions the group does, add it to set
        List<String> playerPerms = player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).collect(Collectors.toList());
        return permGroup.calculateEffectivePermissions(world).stream().filter(s -> !playerPerms.contains(s)).collect(Collectors.toSet());
    }

    public Set<String> calculateDifference(Player player, AssiPermGroup permGroup, String world){
        //                              if group doesn't the permissions the
        List<String> playerPerms = player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).collect(Collectors.toList());
        return playerPerms.stream().filter(s -> !permGroup.calculateEffectivePermissions(world).contains(s)).collect(Collectors.toSet());
    }

    public void playerJoin(Player player){
        if(!loaded(player)){

            PermissionAttachment attachment = player.addAttachment(AssiCore.getCore().getAssiPlugin());
            AssiPermGroup group = getGroupOf(player.getUniqueId());

            calculateDifference(group, player, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, true));
            permissionAttachment.put(player.getUniqueId(), attachment);

        }
    }

    public void playerWorldSwitch(Player player){
        if(loaded(player)){

            PermissionAttachment attachment = player.addAttachment(AssiCore.getCore().getAssiPlugin());
            AssiPermGroup group = getGroupOf(player.getUniqueId());

            calculateDifference(group, player, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, true));
            calculateDifference(player, group, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, false));
            permissionAttachment.put(player.getUniqueId(), attachment);

        }
    }

    public void playerLeave(Player player){
        if(loaded(player)){
            player.removeAttachment(get(player));
            permissionAttachment.remove(player.getUniqueId());
        }
    }

    private boolean loaded(Player player){
        return permissionAttachment.containsKey(player.getUniqueId());
    }

    private PermissionAttachment get(Player player){
        return permissionAttachment.get(player.getUniqueId());
    }

    public AssiPermGroup getGroupOf(UUID uuid){
        try (Connection connection = ModuleManager.getModuleManager().getSQLManager().openConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.GET_PLAYER);
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return getGroup(resultSet.getString("perm_rank"));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            AssiCore.getCore().logE("Failed to get group of "+uuid.toString());
        }
        return null;
    }

    public AssiPermGroup getDefaultGroup(){
        return groups.values().stream().filter(AssiPermGroup::isDefault).limit(1).collect(Collectors.toList()).get(0);
    }

    public void updatePlayerPerms(Player player){

        if(loaded(player)) {

            AssiPermGroup assiPermGroup = getGroupOf(player.getUniqueId());

            if (assiPermGroup != null) {

                PermissionAttachment attachment = get(player);

                calculateDifference(assiPermGroup, player, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, true));
                calculateDifference(player, assiPermGroup, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, false));
            }

        }else{
            AssiCore.getCore().logW(player.getName()+" is not registered under Permissions, attempting to register..");
            playerJoin(player);
        }
    }

    public GroupSyncTask getGroupSyncTask() {
        return groupSyncTask;
    }
}
