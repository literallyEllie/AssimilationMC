package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.AssiBungee;
import com.assimilation.ellie.assibungee.server.AssiPlayer;
import com.assimilation.ellie.assibungee.util.SQLQuery;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PlayerManager implements IManager {

    private SQLManager sqlManager;
    private HashMap<String, AssiPlayer> loadedPlayers;

    @Override
    public boolean load() {
        loadedPlayers = new HashMap<>();

        this.sqlManager = ModuleManager.getModuleManager().getSQLManager();

        return true;
    }

    @Override
    public boolean unload() {

        loadedPlayers.forEach((s, assiPlayer) -> assiPlayer.getBase().disconnect(new TextComponent("&cNetwork offline. We'll be back soon!")));
        loadedPlayers.clear();

        return true;
    }

    public AssiPlayer loadOnlinePlayer(ProxiedPlayer base, boolean createIfNotExist){
        if(!isLoaded(base.getName())){

            try(Connection connection = sqlManager.openConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.GET_PLAYER);
                preparedStatement.setString(1, base.getUniqueId().toString());

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    String name = resultSet.getString("name");
                    String last_ip = resultSet.getString("last_ip");
                    ArrayList<String> names = Util.getGson().fromJson(resultSet.getString("previous_names"), ArrayList.class);
                    String perm_rank = resultSet.getString("perm_rank");
                    int rank = resultSet.getInt("rank");
                    int coins = resultSet.getInt("coins");

                    AssiPlayer assiPlayer = new AssiPlayer(base);

                    if(!base.getName().equals(name)){
                        assiPlayer.getPreviousNames().add(name);
                    }

                    assiPlayer.setPermissionsRank(perm_rank);
                    assiPlayer.setCoins(coins);
                    assiPlayer.setPlayRank(rank);
                    assiPlayer.setIP(last_ip);
                    assiPlayer.setPreviousNames(names);

                    resultSet.close();
                    preparedStatement.close();
                    connection.close();

                    loadedPlayers.put(base.getName().toLowerCase(), assiPlayer);

                    return assiPlayer;

                }else return createIfNotExist ? createPlayer(base) : null;

            }catch(SQLException e){
                e.printStackTrace();
                AssiBungee.getAssiBungee().logE("Failed to get player from SQL. Removing from network to prevent further issues.");
                base.disconnect(new TextComponent(Util.color("&cERROR: Failed to load player object from SQL at ")+e.getLocalizedMessage()));
            }
        }
        return null;
    }

    public AssiPlayer createPlayer(ProxiedPlayer base){
        if(!isLoaded(base.getName())){
            if(!hasEverJoined(base.getUniqueId())){

                AssiPlayer assiPlayer = new AssiPlayer(base);

                try(Connection connection = sqlManager.openConnection()){
                    PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.CREATE_PLAYER);
                    preparedStatement.setString(1, base.getUniqueId().toString());
                    preparedStatement.setString(2, base.getName());
                    preparedStatement.setLong(3, System.currentTimeMillis()); // last seen
                    preparedStatement.setString(4, base.getAddress().getHostString()); //last ip
                    preparedStatement.setString(5, Util.getGson().toJson(Arrays.asList(base.getName())));
                    preparedStatement.setString(6, "default"); //perm rank
                    preparedStatement.setInt(7, 0); // rank
                    preparedStatement.setInt(8, 0); // coins
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.close();
                }catch(SQLException e){
                    e.printStackTrace();
                    AssiBungee.getAssiBungee().logE("Failed to create player in SQL. Removing for network to prevent further issues.");
                    base.disconnect(new TextComponent(Util.color("&cERROR: Failed to create player object into SQL at ")+e.getLocalizedMessage()));
                    return null;
                }
                loadedPlayers.put(assiPlayer.getName().toLowerCase(), assiPlayer);

                return assiPlayer;
            }
        }
        return null;
    }

    public void pushPlayer(AssiPlayer assiPlayer){
        if(!isLoaded(assiPlayer.getName())){

            try(Connection connection = sqlManager.openConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.PUSH_PLAYER);
                preparedStatement.setString(1, assiPlayer.getName());
                preparedStatement.setLong(2, assiPlayer.getLastSeen());
                preparedStatement.setString(3, assiPlayer.getIP());
                preparedStatement.setString(4, Util.getGson().toJson(assiPlayer.getPreviousNames()));
                preparedStatement.setString(5, assiPlayer.getPermissionsRank());
                preparedStatement.setInt(6, assiPlayer.getPlayRank());
                preparedStatement.setInt(7, assiPlayer.getCoins());
                preparedStatement.setString(8, assiPlayer.getUUID().toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();

            }catch(SQLException e){
                e.printStackTrace();
                AssiBungee.getAssiBungee().logE("Failed to push player to SQL database.");
                return;
            }
        }
    }

    public void unloadPlayer(String name){
        if(isLoaded(name)){
            this.pushPlayer(getOnlinePlayer(name));
            this.loadedPlayers.remove(name.toLowerCase());
        }
    }

    public boolean hasEverJoined(UUID uuid){
        boolean found = false;
        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.EXISTS);
            preparedStatement.setString(1, uuid.toString());
            if(preparedStatement.executeQuery().next()){
                found = true;
            }
            preparedStatement.close();
            connection.close();

        }catch(SQLException e){
            e.printStackTrace();
            AssiBungee.getAssiBungee().logE("Failed to check if "+uuid.toString()+"  has ever joined server.");
        }
        return found;
    }

    public UUID getPlayer(String name){

        if(isLoaded(name)){
            return getOnlinePlayer(name).getUUID();
        }

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.EXISTS);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            UUID uuid = null;

            if(resultSet.next()){
                uuid = UUID.fromString(resultSet.getString("uuid"));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

            return uuid;
        }catch(SQLException e){
            e.printStackTrace();
            AssiBungee.getAssiBungee().logE("Failed to get "+name+"'s data.");
        }
        return null;
    }

    public boolean isLoaded(String name){
        return loadedPlayers.containsKey(name.toLowerCase());
    }

    public AssiPlayer getOnlinePlayer(String name){
        return loadedPlayers.get(name.toLowerCase());
    }

    public HashMap<String, AssiPlayer> getLoadedPlayers() {
        return loadedPlayers;
    }

    @Override
    public String getModuleID() {
        return "players";
    }
}
