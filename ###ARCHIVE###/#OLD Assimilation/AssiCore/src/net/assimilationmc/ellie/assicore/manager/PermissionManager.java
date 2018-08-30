package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.command.permission.PCommandManager;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.task.GroupSyncTask;
import net.assimilationmc.ellie.assicore.util.SQLQuery;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.sql2o.Connection;

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
    private int task = -1;
    private boolean update = false;

    private SQLManager sqlManager;
    private PCommandManager pCommandManager;

    @Override
    public boolean load() {
        groups = new HashMap<>();
        permissionAttachment = new HashMap<>();
        sqlManager = ModuleManager.getModuleManager().getSQLManager();

        pCommandManager = new PCommandManager();

        try (Connection connection = sqlManager.getSql2o().open()) {
            connection.createQuery(SQLQuery.PERMISSION.INITIAL_STATEMENT).executeUpdate().close();
        }

        try (Connection connection = sqlManager.getSql2o().open()) {
            int i = 0;
            for (AssiPermGroup assiPermGroup : connection.createQuery(SQLQuery.PERMISSION.GET_GROUPS).executeAndFetch(AssiPermGroup.class)) {
                this.groups.put(assiPermGroup.getName().toLowerCase(), assiPermGroup);
                i++;
            }
            AssiCore.getCore().logI("Loaded " + i + " groups.");
            connection.close();
        }

        groupSyncTask = new GroupSyncTask(this);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(AssiCore.getCore().getAssiPlugin(), groupSyncTask, 10L, 12000L);

        Bukkit.getOnlinePlayers().forEach(this::playerJoin);

        return true;
    }

    @Override
    public boolean unload() {
        if (task != -1) {
            Bukkit.getScheduler().cancelTask(task);
            task = -1;

            if (groupSyncTask != null) {
                groupSyncTask.pushUpdate();
                groupSyncTask = null;
            }
        }

        groups.clear();
        Bukkit.getOnlinePlayers().forEach(o -> o.removeAttachment(permissionAttachment.get(o.getUniqueId())));
        permissionAttachment.clear();
        return true;
    }

    public void createGroup(AssiPermGroup assiPermGroup) {

        if (!isGroup(assiPermGroup.getName())) {
            try (Connection connection = sqlManager.getSql2o().open()) {
                connection.createQuery(SQLQuery.PERMISSION.CREATE_GROUP)
                        .addParameter("groupname", assiPermGroup.getName())
                        .addParameter("prefix", assiPermGroup.getPrefix())
                        .addParameter("suffix", assiPermGroup.getSuffix())
                        .addParameter("parents", Util.getGson().toJson(assiPermGroup.getParents()))
                        .addParameter("options", Util.getGson().toJson(assiPermGroup.getOptions()))
                        .addParameter("permissions", Util.getGson().toJson(assiPermGroup.getPermissions()))
                        .executeUpdate().close();
                this.groups.put(assiPermGroup.getName().toLowerCase(), assiPermGroup);
            }
        }
    }

    public void deleteGroup(AssiPermGroup assiPermGroup) {
        if (isGroup(assiPermGroup.getName())) {
            try (Connection connection = sqlManager.getSql2o().open()) {
                connection.createQuery(SQLQuery.PERMISSION.DELETE_GROUP).addParameter("groupname", assiPermGroup.getName()).executeUpdate().close();
                ModuleManager.getModuleManager().getPlayerManager().group_deleted(assiPermGroup.getName());
                this.groups.remove(assiPermGroup.getName().toLowerCase());
            }
        }
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isGroup(String group) {
        return this.groups.containsKey(group.toLowerCase());
    }

    @Override
    public String getModuleID() {
        return "permission";
    }

    public HashMap<String, AssiPermGroup> getGroups() {
        return groups;
    }

    public AssiPermGroup getGroup(String id) {
        return groups.get(id.toLowerCase());
    }

    public Set<String> calculateDifference(AssiPermGroup permGroup, Player player, String world) {
        //                             if the player doesn't have the permissions the group does, add it to set
        List<String> playerPerms = player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).collect(Collectors.toList());
        return permGroup.calculateEffectivePermissions(world).stream().filter(s -> !playerPerms.contains(s)).collect(Collectors.toSet());
    }

    public Set<String> calculateDifference(Player player, AssiPermGroup permGroup, String world) {
        //                              if group doesn't the permissions the
        List<String> playerPerms = player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).collect(Collectors.toList());
        return playerPerms.stream().filter(s -> !permGroup.calculateEffectivePermissions(world).contains(s)).collect(Collectors.toSet());
    }

    public void playerJoin(Player player) {
        if (!loaded(player)) {

            PermissionAttachment attachment = player.addAttachment(AssiCore.getCore().getAssiPlugin());
            AssiPermGroup group = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(player.getUniqueId());

            calculateDifference(group, player, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, true));
            permissionAttachment.put(player.getUniqueId(), attachment);

        }
    }

    public void playerWorldSwitch(Player player) {
        if (loaded(player)) {

            PermissionAttachment attachment = player.addAttachment(AssiCore.getCore().getAssiPlugin());
            AssiPermGroup group = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(player.getUniqueId());

            calculateDifference(group, player, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, true));
            calculateDifference(player, group, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, false));
            permissionAttachment.put(player.getUniqueId(), attachment);
        }
    }

    public void playerLeave(Player player) {
        if (loaded(player)) {
            player.removeAttachment(get(player));
            permissionAttachment.remove(player.getUniqueId());
        }
    }

    private boolean loaded(Player player) {
        return permissionAttachment.containsKey(player.getUniqueId());
    }

    private PermissionAttachment get(Player player) {
        return permissionAttachment.get(player.getUniqueId());
    }


    public AssiPermGroup getDefaultGroup() throws IndexOutOfBoundsException {
        return groups.values().stream().filter(AssiPermGroup::isDefault).limit(1).collect(Collectors.toList()).get(0);
    }

    public void updatePlayerPerms(Player player) {

        if (loaded(player)) {

            AssiPermGroup assiPermGroup = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(player.getUniqueId());

            if (assiPermGroup != null) {

                PermissionAttachment attachment = get(player);

                calculateDifference(assiPermGroup, player, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, true));
                calculateDifference(player, assiPermGroup, player.getWorld().getName()).forEach(s -> attachment.setPermission(s, false));
            }

        } else {
            AssiCore.getCore().logW(player.getName() + " is not registered under Permissions, attempting to register..");
            playerJoin(player);
        }
    }

    public GroupSyncTask getGroupSyncTask() {
        return groupSyncTask;
    }

    public PCommandManager getPCommandManager() {
        return pCommandManager;
    }

}
