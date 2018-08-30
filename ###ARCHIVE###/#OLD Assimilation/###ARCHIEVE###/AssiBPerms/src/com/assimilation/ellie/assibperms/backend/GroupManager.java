package com.assimilation.ellie.assibperms.backend;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.permission.GroupOption;
import com.assimilation.ellie.assibperms.task.GroupSyncTask;
import com.assimilation.ellie.assibperms.util.SQLQuery;
import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.manager.SQLManager;
import com.assimilation.ellie.assibungee.util.Util;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 22/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GroupManager {

    protected SQLManager sqlManager;

    private GroupSyncTask groupSyncTask;
    private ScheduledTask task;

    private boolean update;

    private LinkedHashMap<String, AssiPermGroup> loadedGroups;

    public GroupManager(){
        sqlManager = ModuleManager.getModuleManager().getSQLManager();
        loadedGroups = new LinkedHashMap<>();

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.INITIAL_STATEMENT);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to make initial statement to database! (Bungee)");
            return;
        }

        groupSyncTask = new GroupSyncTask(this);

        task = ProxyServer.getInstance().getScheduler().schedule(AssiBPerms.getAssiBPerms(), groupSyncTask, 1, 10, TimeUnit.MINUTES);

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.SPIGOT.INITIAL_STATEMENT);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to make initial statement to database! (Spigot)");
            return;
        }

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement statement = connection.prepareStatement(SQLQuery.GET_GROUPS);
            ResultSet resultSet = statement.executeQuery();

            int i = 0;
            while(resultSet.next()){

                String name = resultSet.getString("group_name");
                String prefix = resultSet.getString("prefix");
                String suffix = resultSet.getString("suffix");

                Type pJson = new TypeToken<Set<String>>() {}.getType();
                Set<String> parents = Util.getGson().fromJson(resultSet.getString("parents"), pJson);

                Type permJson = new TypeToken<Set<String>>() {}.getType();
                Set<String> permissions = Util.getGson().fromJson(resultSet.getString("permissions"), permJson);

                Type optionJson = new TypeToken<Set<GroupOption>>() {}.getType();
                Set<GroupOption> options = Util.getGson().fromJson(resultSet.getString("options"), optionJson);

                AssiPermGroup assiPermGroup = new AssiPermGroup(name, parents);
                assiPermGroup.setPrefix(prefix);
                assiPermGroup.setSuffix(suffix);
                assiPermGroup.setPermissions(permissions);
                assiPermGroup.setOptions(options);

                this.loadedGroups.put(name.toLowerCase(), assiPermGroup);
                i++;
            }

            AssiBPerms.getAssiBPerms().logI("Loaded "+i+" groups.");

            resultSet.close();
            statement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to get groups");
        }
    }

    public void finish(){
        if(task != null){
            task.cancel();
            task = null;

            if(groupSyncTask != null){
                groupSyncTask.pushUpdate();
                groupSyncTask = null;
            }
        }

        this.sqlManager = null;
        this.loadedGroups.clear();
    }

    public void createGroup(AssiPermGroup assiPermGroup){

        if(!isGroup(assiPermGroup.getName())) {
            try (Connection connection = sqlManager.openConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.CREATE_GROUP);
                preparedStatement.setString(1, assiPermGroup.getName());
                preparedStatement.setString(2, assiPermGroup.getPrefix());
                preparedStatement.setString(3, assiPermGroup.getSuffix());
                preparedStatement.setString(4, Util.getGson().toJson(assiPermGroup.getParents()));
                preparedStatement.setString(5, Util.getGson().toJson(assiPermGroup.getOptions()));
                preparedStatement.setString(6, Util.getGson().toJson(assiPermGroup.getPermissions()));

                preparedStatement.executeUpdate();
                connection.close();
                this.loadedGroups.put(assiPermGroup.getName().toLowerCase(), assiPermGroup);

                this.spigot_putGroup(assiPermGroup);
            } catch (SQLException e) {
                e.printStackTrace();
                AssiBPerms.getAssiBPerms().logE("Failed to export group to SQL " + assiPermGroup.getName());
            }
        }

    }

    public void deleteGroup(AssiPermGroup assiPermGroup){

        if(isGroup(assiPermGroup.getName())) {
            try (Connection connection = sqlManager.openConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.DELETE_GROUP);
                preparedStatement.setString(1, assiPermGroup.getName());
                preparedStatement.executeUpdate();

                connection.close();
                this.spigot_deleteGroup(assiPermGroup.getName());
                AssiBPerms.getAssiBPerms().getUserManager().group_deleted(assiPermGroup.getName());

                this.loadedGroups.remove(assiPermGroup.getName().toLowerCase());
            } catch (SQLException e) {
                e.printStackTrace();
                AssiBPerms.getAssiBPerms().logE("Failed to remove group from SQL " + assiPermGroup.getName());
            }
        }
    }

    public AssiPermGroup getGroup(String group){
        return this.loadedGroups.get(group.toLowerCase());
    }

    public boolean isGroup(String group){
        return this.loadedGroups.containsKey(group.toLowerCase());
    }

    public AssiPermGroup getDefaultGroup(){
        return loadedGroups.values().stream().filter(AssiPermGroup::isDefault).limit(1).collect(Collectors.toList()).get(0);
    }

    public HashMap<String, AssiPermGroup> getLoadedGroups() {
        return loadedGroups;
    }

    private void spigot_putGroup(AssiPermGroup assiPermGroup){
        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.SPIGOT.CREATE_GROUP);
            preparedStatement.setString(1, assiPermGroup.getName());
            preparedStatement.setString(2, Util.getGson().toJson(assiPermGroup.getSpigotPermissions()));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to export group to Spigot SQL table (put)");
        }
    }

    public void spigot_updateGroup(AssiPermGroup assiPermGroup){
        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.SPIGOT.PUSH_GROUP);
            preparedStatement.setString(1, Util.getGson().toJson(assiPermGroup.getSpigotPermissions()));
            preparedStatement.setString(2, assiPermGroup.getName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to export group to Spigot SQL table (update)");
        }
    }

    private void spigot_deleteGroup(String group){
        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.SPIGOT.DELETE_GROUP);
            preparedStatement.setString(1, group);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to export group to Spigot SQL table (delete)");
        }
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public GroupSyncTask getGroupSyncTask() {
        return groupSyncTask;
    }

}
