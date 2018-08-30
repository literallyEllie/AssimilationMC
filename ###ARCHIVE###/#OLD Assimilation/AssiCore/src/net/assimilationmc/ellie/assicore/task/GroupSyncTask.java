package net.assimilationmc.ellie.assicore.task;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.manager.PermissionManager;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.util.SQLQuery;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.sql2o.Connection;

/**
 * Created by Ellie on 15/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GroupSyncTask implements Runnable {

    private PermissionManager permissionManager;

    public GroupSyncTask(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public void run() {

        if (permissionManager.isUpdate()) {
            pushUpdate();
            AssiCore.getCore().logI("[Done] Synced ranks");
            Bukkit.getOnlinePlayers().forEach(player -> permissionManager.updatePlayerPerms(player));
            AssiCore.getCore().logI("[Done] Updated permissions for online players");
        } else {
            AssiCore.getCore().logI("Sync seen not necessary.");
        }

    }

    public void pushUpdate() {
        AssiCore.getCore().logI("Syncing ranks with database...");
        try(Connection connection = ModuleManager.getModuleManager().getSQLManager().getSql2o().open()){
            for (AssiPermGroup assiPermGroup : permissionManager.getGroups().values()) {
                connection.createQuery(SQLQuery.PERMISSION.PUSH_GROUP)
                .addParameter("prefix", assiPermGroup.getPrefix())
                .addParameter("suffix", assiPermGroup.getSuffix())
                .addParameter("parents", Util.getGson().toJson(assiPermGroup.getParents()))
                .addParameter("options", Util.getGson().toJson(assiPermGroup.getOptions()))
                .addParameter("permissions", Util.getGson().toJson(assiPermGroup.getPermissions()))
                .addParameter("name", assiPermGroup.getName()).executeUpdate();
            }
            connection.close();
        }

        Bukkit.getOnlinePlayers().forEach(player -> permissionManager.updatePlayerPerms(player));
        AssiCore.getCore().logI("[Done] Updated permissions");

    }
}
