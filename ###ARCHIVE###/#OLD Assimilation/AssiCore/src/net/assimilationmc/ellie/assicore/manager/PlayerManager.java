package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.util.SQLQuery;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sql2o.Connection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        loadedPlayers.forEach((s, assiPlayer) -> assiPlayer.getBase().kickPlayer(Util.prefix()+"\n\n"+Util.color("&cThe server has now gone offline offline.\n &cWe'll be back soon!")));
        loadedPlayers.clear();

        return true;
    }

    public AssiPlayer loadOnlinePlayer(Player base, boolean createIfNotExist){
        if(!isLoaded(base.getName())){

            try(Connection connection = sqlManager.getSql2o().open()){
                List<AssiPlayer> players = connection.createQuery(SQLQuery.PLAYERS.GET_PLAYER).addParameter("uuid", base.getUniqueId().toString()).
                        executeAndFetch(AssiPlayer.class);
                connection.close();
                if(!players.isEmpty()){
                    AssiPlayer assiPlayer = players.get(0);
                    if(!base.getName().equals(assiPlayer.getName())){
                        assiPlayer.getPreviousNames().add(assiPlayer.getName());
                        assiPlayer.setName(base.getName());
                    }
                    assiPlayer.setLast_ip(base.getAddress().getAddress().getHostAddress());
                    assiPlayer.setLast_seen(System.currentTimeMillis());
                    assiPlayer.setBase(base);
                    assiPlayer.setUuid(base.getUniqueId());
                    loadedPlayers.put(base.getName().toLowerCase(), assiPlayer);
                    return players.get(0);

                }else return createIfNotExist ? createPlayer(base) : null;
            }
        }
        return null;
    }

    public AssiPlayer createPlayer(Player base){
        if(!isLoaded(base.getName())){
            if(!hasEverJoined(base.getUniqueId())){
                AssiPlayer assiPlayer = new AssiPlayer(base);
                assiPlayer.setPerm_rank("User");
                assiPlayer.setName(base.getName());
                assiPlayer.setLast_ip(base.getAddress().getAddress().getHostAddress());
                assiPlayer.setLast_seen(System.currentTimeMillis());

                try(Connection connection = sqlManager.getSql2o().open()) {
                    connection.createQuery(SQLQuery.PLAYERS.CREATE_PLAYER).
                            addParameter("uuid", base.getUniqueId().toString()).
                            addParameter("name", base.getName()).
                            addParameter("last_seen", System.currentTimeMillis()).
                            addParameter("last_ip", base.getAddress().getAddress().getHostAddress()).
                            addParameter("previous_names", Util.getGson().toJson(Arrays.asList(base.getName()))).
                            addParameter("perm_rank", ModuleManager.getModuleManager().getPermissionManager().getDefaultGroup().getName()).
                            addParameter("coins", 0).
                            executeUpdate().close();
                    loadedPlayers.put(assiPlayer.getName().toLowerCase(), assiPlayer);
                }
                return assiPlayer;
            }
        }
        return null;
    }

    public void pushPlayer(AssiPlayer assiPlayer){
        if(isLoaded(assiPlayer.getName())){

            try(Connection connection = sqlManager.getSql2o().open()){
                connection.createQuery(SQLQuery.PLAYERS.PUSH_PLAYER).
                addParameter("uuid", assiPlayer.getUuid().toString()).
                        addParameter("name", assiPlayer.getName()).
                        addParameter("last_seen", System.currentTimeMillis()).
                        addParameter("last_ip", assiPlayer.getBase().getAddress().getAddress().getHostAddress()).
                        addParameter("previous_names", Util.getGson().toJson(assiPlayer.getPreviousNames())).
                        addParameter("perm_rank", assiPlayer.getPermissionsRank()).
                        addParameter("coins", assiPlayer.getCoins()).
                        executeUpdate().close();
                connection.close();
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
        boolean found;
        try(Connection connection = sqlManager.getSql2o().open()) {
            found = !connection.createQuery(SQLQuery.PLAYERS.EXISTS).addParameter("uuid", uuid.toString()).executeAndFetch(AssiPlayer.class).isEmpty();
            connection.close();
        }
        return found;
    }

    public AssiPlayer getPlayer(UUID uuid){
        if(Bukkit.getPlayer(uuid) != null){
            return getOnlinePlayer(Bukkit.getPlayer(uuid).getName());
        }
        try(Connection connection = sqlManager.getSql2o().open()){
            List<AssiPlayer> players = connection.createQuery(SQLQuery.PLAYERS.EXISTS).
                    addParameter("uuid", uuid.toString()).executeAndFetch(AssiPlayer.class);
            if(!players.isEmpty()){
                AssiPlayer assiPlayer = players.get(0);
                connection.close();
                return assiPlayer;
            }
        }
        return null;
    }

    public AssiPlayer getPlayerData(UUID uuid){
        if(Bukkit.getPlayer(uuid) != null){
            return getOnlinePlayer(Bukkit.getPlayer(uuid).getName());
        }
        try(Connection connection = sqlManager.getSql2o().open()){
            List<AssiPlayer> players = connection.createQuery(SQLQuery.PLAYERS.GET_PLAYER).
                    addParameter("uuid", uuid.toString()).executeAndFetch(AssiPlayer.class);
            if(!players.isEmpty()){
                AssiPlayer assiPlayer = players.get(0);
                connection.close();
                return assiPlayer;
            }
        }
        return null;
    }

    public synchronized UUID getPlayer(String name){
        if(isLoaded(name)){
            return getOnlinePlayer(name).getUuid();
        }
        try(Connection connection = sqlManager.getSql2o().open()){
            List<AssiPlayer> assiPlayers = connection.createQuery(SQLQuery.PLAYERS.EXISTS_NAME).
                    addParameter("name", name).executeAndFetch(AssiPlayer.class);
            connection.close();
            if(!assiPlayers.isEmpty()){
                return assiPlayers.get(0).getUuid();
            }
        }
        return null;
    }

    void group_deleted(String group){

        // remove group from all users

        try {
            AssiPermGroup defaultGroup = ModuleManager.getModuleManager().getPermissionManager().getDefaultGroup();
            if (defaultGroup != null) {
                Bukkit.getOnlinePlayers().forEach(proxiedPlayer -> {
                    if (ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(proxiedPlayer.getName()).getPermissionsRank().equalsIgnoreCase(group)) {
                        setGroup(defaultGroup, proxiedPlayer.getUniqueId());
                        Util.mWARN(proxiedPlayer, "Your rank has been deleted, you have been moved to the default rank. If you believe if this is an issue do /helpop to contact staff");
                    }
                });
            } else {
                // ModuleManager.getModuleManager().getStaffChatManager().message(ProxyServer.getInstance().getConsole(), "AUTO", "&cWarning: the default group was removed, this will cause severe issues.");
                Bukkit.getOnlinePlayers().forEach(proxiedPlayer -> {
                    if (ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(proxiedPlayer.getName()).getPermissionsRank().equalsIgnoreCase(group)) {
                        Util.mWARN(proxiedPlayer, "A severe error has occured (could take 10 minutes to take effect) " +
                                "and this may restrict your access to the server. It is recommended you disconnect to prevent further issues");
                    }
                });
            }
        }catch(IndexOutOfBoundsException e){}
    }

    public AssiPermGroup getGroupOf(UUID uuid){
        if(Bukkit.getPlayer(uuid) != null && getOnlinePlayer(Bukkit.getPlayer(uuid).getName()) != null){
            Player player = Bukkit.getPlayer(uuid);
            return ModuleManager.getModuleManager().getPermissionManager().getGroup(getOnlinePlayer(player.getName()).getPermissionsRank());
        }

        try (Connection connection = ModuleManager.getModuleManager().getSQLManager().getSql2o().open()) {
            List<AssiPlayer> a = connection.createQuery(SQLQuery.PLAYERS.GET_PLAYER).addParameter("uuid", uuid.toString()).executeAndFetch(AssiPlayer.class);
            connection.close();
            if(!a.isEmpty()){
                return ModuleManager.getModuleManager().getPermissionManager().getGroup(a.get(0).getPermissionsRank());
            }
        }
        return null;
    }

    public void setGroup(AssiPermGroup group, UUID uuid){

        if(Bukkit.getPlayer(uuid) != null && getOnlinePlayer(Bukkit.getPlayer(uuid).getName()) != null){
            getOnlinePlayer(Bukkit.getPlayer(uuid).getName()).setPerm_rank(group.getName());
            System.out.println("user is "+ uuid.toString());
            return;
        }

        try (Connection connection = sqlManager.getSql2o().open()) {
            connection.createQuery(SQLQuery.PLAYERS.PUSH_PLAYER_GROUP).addParameter("perm_rank", group.getName()).addParameter("uuid", uuid.toString()).executeUpdate().close();
        }
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

    public HashMap<UUID, String> getIPUsers(String ip){
        HashMap<UUID, String> users  = new HashMap<>();
        try(Connection connection = sqlManager.getSql2o().open()){
            connection.createQuery(SQLQuery.PLAYERS.IP_USERS).addParameter("ip", ip).executeAndFetch(AssiPlayer.class)
            .forEach(assiPlayer -> users.put(assiPlayer.getUuid(), assiPlayer.getName()));
            connection.close();
        }
        return users;

    }

}
