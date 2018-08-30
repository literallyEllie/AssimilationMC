package com.assimilation.ellie.assibperms.backend;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.util.SQLQuery;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UserManager {

    private GroupManager groupManager;

    public UserManager(GroupManager groupManager){
        this.groupManager = groupManager;
    }

    public AssiPermGroup getGroupOf(UUID uuid){
        try (Connection connection = groupManager.sqlManager.openConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(com.assimilation.ellie.assibungee.util.SQLQuery.PLAYERS.GET_PLAYER);
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return groupManager.getGroup(resultSet.getString("perm_rank"));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to get group of "+uuid.toString());
        }
        return null;
    }

    public void setGroup(AssiPermGroup group, UUID uuid){

        try (Connection connection = groupManager.sqlManager.openConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.PUSH_PLAYER_GROUP);

            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            AssiBPerms.getAssiBPerms().logE("Failed to set group of "+uuid.toString());
        }
    }

    void group_deleted(String group){

        // remove group from all users

        AssiPermGroup defaultGroup = groupManager.getDefaultGroup();
        if(defaultGroup != null) {
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
                if(ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(proxiedPlayer.getName()).getPermissionsRank().equalsIgnoreCase(group)){
                    setGroup(defaultGroup, proxiedPlayer.getUniqueId());
                    Util.mWARN(proxiedPlayer, "Your rank has been deleted, you have been moved to the default rank. If you believe if this is an issue do /helpop to contact staff");
                }
            });
        }else {
            ModuleManager.getModuleManager().getStaffChatManager().message(ProxyServer.getInstance().getConsole(), "AUTO", "&cWarning: the default group was removed, this will cause severe issues.");
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
                if(ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(proxiedPlayer.getName()).getPermissionsRank().equalsIgnoreCase(group)){
                    Util.mWARN(proxiedPlayer, "A severe error has occured (could take 10 minutes to take effect) " +
                            "and this may restrict your access to the server. It is recommended you disconnect to prevent further issues");
                }
            });
        }

    }

    public void updatePlayerPerms(ProxiedPlayer proxiedPlayer){

        AssiPermGroup assiPermGroup = getGroupOf(proxiedPlayer.getUniqueId());

        if(assiPermGroup == null) {
            assiPermGroup = groupManager.getDefaultGroup();
            if(assiPermGroup != null){
                setGroup(assiPermGroup, proxiedPlayer.getUniqueId());
                AssiBPerms.getAssiBPerms().logI("Set player group "+proxiedPlayer.getName()+" to the default group since they had none.");
            }
        }

        if(assiPermGroup != null) {
            calculateDifference(assiPermGroup, proxiedPlayer).forEach(s -> proxiedPlayer.setPermission(s, true));
            calculateDifference(proxiedPlayer, assiPermGroup).forEach(s -> proxiedPlayer.setPermission(s, false));
        }
    }

    public Set<String> calculateDifference(AssiPermGroup permGroup, ProxiedPlayer player){
        //                              if the player doesn't have the permissions the group does, add it to set
        return permGroup.calculateEffectivePermissions().stream().filter(s -> !player.getPermissions().contains(s)).collect(Collectors.toSet());
    }

    public Set<String> calculateDifference( ProxiedPlayer player, AssiPermGroup permGroup){
        //                              if group doesn't the permissions the
        return player.getPermissions().stream().filter(s -> !permGroup.calculateEffectivePermissions().contains(s)).collect(Collectors.toSet());
    }


}
