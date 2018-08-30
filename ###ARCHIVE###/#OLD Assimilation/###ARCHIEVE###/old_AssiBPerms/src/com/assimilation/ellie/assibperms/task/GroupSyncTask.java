package com.assimilation.ellie.assibperms.task;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.backend.GroupManager;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.util.SQLQuery;
import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.ProxyServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GroupSyncTask implements Runnable {

    private GroupManager groupManager;

    public GroupSyncTask(GroupManager groupManager){
        this.groupManager = groupManager;
    }

    @Override
    public void run() {

        if(groupManager.isUpdate()) {
            pushUpdate();
            AssiBPerms.getAssiBPerms().logI("[Done] Synced ranks");
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> AssiBPerms.getAssiBPerms().getUserManager().updatePlayerPerms(proxiedPlayer));
            AssiBPerms.getAssiBPerms().logI("[Done] Synced online player permissions");
        }else{
            AssiBPerms.getAssiBPerms().logI("Sync seen not necessary.");
        }
    }

    public void pushUpdate(){
        AssiBPerms.getAssiBPerms().logI("Syncing ranks with database...");
        try(Connection connection = ModuleManager.getModuleManager().getSQLManager().openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PUSH_GROUP);


            for (AssiPermGroup assiPermGroup : groupManager.getLoadedGroups().values()) {
                preparedStatement.setString(1, assiPermGroup.getPrefix());
                preparedStatement.setString(2, assiPermGroup.getSuffix());
                preparedStatement.setString(3, Util.getGson().toJson(assiPermGroup.getParents()));
                preparedStatement.setString(4, Util.getGson().toJson(assiPermGroup.getOptions()));
                preparedStatement.setString(5, Util.getGson().toJson(assiPermGroup.getPermissions()));
                preparedStatement.setString(6, assiPermGroup.getName());
                preparedStatement.executeUpdate();
            }

            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to sync the groups to the SQL");
        }

        groupManager.getLoadedGroups().values().forEach(assiPermGroup -> groupManager.spigot_updateGroup(assiPermGroup));

    }

}
