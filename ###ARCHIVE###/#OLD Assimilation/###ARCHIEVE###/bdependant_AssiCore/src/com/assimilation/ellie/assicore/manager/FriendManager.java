package com.assimilation.ellie.assicore.manager;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.util.JsonUtil;
import com.assimilation.ellie.assicore.util.MessageLib;
import com.assimilation.ellie.assicore.util.SQLQuery;
import com.assimilation.ellie.assicore.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class FriendManager implements IManager {

    private AssiCore assiCore;

    public FriendManager(AssiCore assiCore){
        this.assiCore = assiCore;


    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public String getModuleID() {
        return "friend";
    }

    public String addFriend(UUID sender, UUID target){
        if(isFriend(sender, target)){
            return MessageLib.FRIEND.ALREADY_FRIEND;
        }

        try(Connection connection = ModuleManager.getModuleManager().getSQLManager().openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.FRIENDS.GET_FRIENDS);
            preparedStatement.setString(1, sender.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            //update player friend list @sender

            String friends = resultSet.getString("friends");
            resultSet.close();
            preparedStatement.close();

            String newList = JsonUtil.array_append(friends, target.toString());

            PreparedStatement updateStmt = connection.prepareStatement(SQLQuery.FRIENDS.PUSH_FRIENDS);
            updateStmt.setString(1, newList);
            updateStmt.setString(2, sender.toString());
            updateStmt.executeUpdate();
            updateStmt.close();


            //update player friend list @target
            PreparedStatement tStmt = connection.prepareStatement(SQLQuery.FRIENDS.GET_FRIENDS);
            tStmt.setString(1, target.toString());
            ResultSet tRs = preparedStatement.executeQuery();

            //update player friend list @sender

            String tVenn = tRs.getString("friends");
            tRs.close();
            tStmt.close();

            String vennList = JsonUtil.array_append(tVenn, sender.toString());

            PreparedStatement tUpdateStmt = connection.prepareStatement(SQLQuery.FRIENDS.PUSH_FRIENDS);
            tUpdateStmt.setString(1, vennList);
            tUpdateStmt.setString(2, target.toString());
            tUpdateStmt.executeUpdate();
            tUpdateStmt.close();


            connection.close();

        }catch(SQLException e){
            Util.handleException("sql add friend", e);
            return String.format(MessageLib.COMMAND_FAIL, "Failed to add friend at SQL");
        }


        return MessageLib.FRIEND.FRIEND_ACCEPTED;
    }

    public String removeFriend(UUID sender, UUID target) {
        if (!isFriend(sender, target)){
            return MessageLib.FRIEND.NOT_FRIENDS;
        }

        try(Connection connection = ModuleManager.getModuleManager().getSQLManager().openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.FRIENDS.GET_FRIENDS);
            preparedStatement.setString(1, sender.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            //update player friend list @sender

            String friends = resultSet.getString("friends");
            resultSet.close();
            preparedStatement.close();

            String newList = JsonUtil.array_remove(friends, target.toString());

            PreparedStatement updateStmt = connection.prepareStatement(SQLQuery.FRIENDS.PUSH_FRIENDS);
            updateStmt.setString(1, newList);
            updateStmt.setString(2, sender.toString());
            updateStmt.executeUpdate();
            updateStmt.close();


            //update player friend list @target
            PreparedStatement tStmt = connection.prepareStatement(SQLQuery.FRIENDS.GET_FRIENDS);
            tStmt.setString(1, target.toString());
            ResultSet tRs = preparedStatement.executeQuery();

            //update player friend list @sender

            String tVenn = tRs.getString("friends");
            tRs.close();
            tStmt.close();

            String vennList = JsonUtil.array_remove(tVenn, sender.toString());

            PreparedStatement tUpdateStmt = connection.prepareStatement(SQLQuery.FRIENDS.PUSH_FRIENDS);
            tUpdateStmt.setString(1, vennList);
            tUpdateStmt.setString(2, target.toString());
            tUpdateStmt.executeUpdate();
            tUpdateStmt.close();


            connection.close();
        }catch(SQLException e){
            Util.handleException("sql remove friend", e);
            return String.format(MessageLib.COMMAND_FAIL, "Failed to remove friend at SQL");
        }


        return MessageLib.FRIEND.FRIEND_REMOVED;
    }

    public boolean isFriend(UUID uuid, UUID query){
        try(Connection connection = ModuleManager.getModuleManager().getSQLManager().openConnection()){

            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.FRIENDS.GET_FRIENDS);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            String friends = resultSet.getString("friends");
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return JsonUtil.array_contains(friends, query.toString());
        }catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



}
