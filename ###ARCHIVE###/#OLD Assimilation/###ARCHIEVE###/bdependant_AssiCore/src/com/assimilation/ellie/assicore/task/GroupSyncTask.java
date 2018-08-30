package com.assimilation.ellie.assicore.task;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.manager.PermissionManager;
import com.assimilation.ellie.assicore.permission.AssiPermGroup;
import com.assimilation.ellie.assicore.permission.GroupOption;
import com.assimilation.ellie.assicore.permission.SpigotPermission;
import com.assimilation.ellie.assicore.util.SQLQuery;
import com.assimilation.ellie.assicore.util.Util;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Ellie on 15/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GroupSyncTask implements Runnable {

    private PermissionManager permissionManager;

    public GroupSyncTask(PermissionManager permissionManager){
        this.permissionManager = permissionManager;
    }

    @Override
    public void run() {

        try(Connection connection = ModuleManager.getModuleManager().getSQLManager().openConnection()){

            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PERMISSION.BUNGEE_GET_GROUPS);
            ResultSet resultSet = preparedStatement.executeQuery();

            HashMap<String, AssiPermGroup> temp_group = new HashMap<>();

            while(resultSet.next()){

                String name = resultSet.getString("group_name");
                String prefix = resultSet.getString("prefix");
                String suffix = resultSet.getString("suffix");

                Type pJson = new TypeToken<Set<String>>() {}.getType();
                Set<String> parents = Util.getGson().fromJson(resultSet.getString("parents"), pJson);

                Type optionJson = new TypeToken<Set<GroupOption>>() {}.getType();
                Set<GroupOption> options = Util.getGson().fromJson(resultSet.getString("options"), optionJson);

                AssiPermGroup assiPermGroup = new AssiPermGroup(name, parents);
                assiPermGroup.setPrefix(prefix);
                assiPermGroup.setSuffix(suffix);
                assiPermGroup.setOptions(options);
                temp_group.put(name.toLowerCase(), assiPermGroup);
            }

            resultSet.close();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement(SQLQuery.PERMISSION.SPIGOT_GET_GROUPS);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                String name = resultSet.getString("group_name");

                if(temp_group.get(name.toLowerCase()) != null){

                    Type pJson = new TypeToken<Set<SpigotPermission>>() {}.getType();
                    Set<SpigotPermission> permissions = Util.getGson().fromJson(resultSet.getString("permissions"), pJson);

                    String server = AssiCore.getCore().getServerID();
                    for(Iterator<SpigotPermission> permissionIterator = permissions.iterator(); permissionIterator.hasNext();){
                        if(!permissionIterator.next().onServer(server)){
                            permissionIterator.remove();
                            System.out.println("removed");
                        }
                    }

                    temp_group.get(name.toLowerCase()).setPermissions(permissions);



                }else{
                    AssiCore.getCore().logW("Spigot perm table contains group '"+name+"' that may not exist in master table");
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();


            permissionManager.getGroups().clear();
            permissionManager.getGroups().putAll(temp_group);
            temp_group.clear();

        }catch(SQLException e){
            e.printStackTrace();
            AssiCore.getCore().logW("Failed to get updates from database!");
        }

        Bukkit.getOnlinePlayers().forEach(player -> permissionManager.updatePlayerPerms(player));
        AssiCore.getCore().logI("[Done] Updated permissions");

    }




}
