package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.AssiBungee;
import com.assimilation.ellie.assibungee.server.AssiPlayer;
import com.assimilation.ellie.assibungee.server.ServerState;
import com.assimilation.ellie.assibungee.server.ServerType;
import com.assimilation.ellie.assibungee.task.ServerStatusTask;
import com.assimilation.ellie.assibungee.util.AssiServerInfo;
import com.assimilation.ellie.assibungee.util.Callback;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ServerManager implements IManager, Callback<LinkedHashMap<String, AssiServerInfo>> {

    private LinkedHashMap<String, AssiServerInfo> servers;
    private ScheduledTask bungeeTask;

    @Override
    public boolean load() {
        this.servers = new LinkedHashMap<>();

        ServerStatusTask serverStatusTask = new ServerStatusTask(this, servers);

        this.bungeeTask = ProxyServer.getInstance().getScheduler().schedule(AssiBungee.getAssiBungee(), serverStatusTask, 1, 10, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean unload() {

        servers.clear();

        if(this.bungeeTask != null){
            this.bungeeTask.cancel();
            this.bungeeTask = null;
        }

        return true;
    }

    @Override
    public String getModuleID() {
        return "server";
    }

    void putServer(AssiServerInfo info){
        if(!this.servers.containsKey(info.getID()) && Util.filter(servers.keySet(), info.getID()).size() == 0){
            this.servers.put(info.getID(), info);
        }
    }

    public void updateServer(String server, ServerState serverState){
        if(this.servers.get(server) != null) {
            this.servers.get(server).setServerState(serverState);
            ModuleManager.getModuleManager().getConfigManager().updateServers(servers.values());
        }

    }

    public AssiServerInfo findPlayer(String player) {
        if (ProxyServer.getInstance().getPlayer(player) != null) {
            return servers.get(ProxyServer.getInstance().getPlayer(player).getServer().getInfo().getName());
        }
        return null;
    }

    public void sendPlayer(AssiPlayer assiPlayer, AssiServerInfo serverInfo){
        ProxyServer.getInstance().getPlayer(assiPlayer.getUUID()).connect(serverInfo.getServer());
    }

    public boolean canJoin(ProxiedPlayer player, String server) {
        AssiServerInfo servera = getServer(server);

        if(servera != null && !servera.getServerState().equals(ServerState.STABLE)){

            if(player.hasPermission(PermissionLib.BYPASS.MAINTENANCE)){
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean sendLobby(ProxiedPlayer player){
        List<AssiServerInfo> a = servers.values().stream().filter(serverInfo -> serverInfo.getServerType().equals(ServerType.LOBBY)).filter(serverInfo ->
            serverInfo.getServerState().equals(ServerState.STABLE)).collect(Collectors.toList());

        if(a.size() == 0){
            return false;
        }

        String server = a.get(0).getID();
        int players = a.get(0).getServer().getPlayers().size();

        for (AssiServerInfo assiServerInfo : a) {
            if(assiServerInfo.getServer().getPlayers().size() < players){
                players = assiServerInfo.getServer().getPlayers().size();
                server = assiServerInfo.getID();
            }
        }

        player.connect(getServer(server).getServer());
        Util.mINFO(player, "You have been connected to &9"+server+"&f.");
        return true;
    }

    public ServerInfo getNextLobby(){

        List<AssiServerInfo> a = servers.values().stream().filter(serverInfo -> serverInfo.getServerType().equals(ServerType.LOBBY)).filter(serverInfo ->
                serverInfo.getServerState().equals(ServerState.STABLE)).collect(Collectors.toList());

        if(a.size() == 0){
            return null;
        }

        String server = a.get(0).getID();
        int players = a.get(0).getServer().getPlayers().size();

        for (AssiServerInfo assiServerInfo : a) {
            if(assiServerInfo.getServer().getPlayers().size() < players){
                players = assiServerInfo.getServer().getPlayers().size();
                server = assiServerInfo.getID();
            }
        }

        return ProxyServer.getInstance().getServerInfo(server);
    }

    public AssiServerInfo getServer(String id){
        List<String> a = Util.filterCapped(servers.keySet(), id, 1);
        if(a != null || a.size() != 0 ) {
            for (String s : a) {
                return servers.get(s);
            }
        }
        return null;
    }

    public ScheduledTask getTask() {
        return bungeeTask;
    }

    public LinkedHashMap<String, AssiServerInfo> getServers() {
        return servers;
    }

    @Override
    public void run(LinkedHashMap<String, AssiServerInfo> data) {
        this.servers = data;
    }


}
