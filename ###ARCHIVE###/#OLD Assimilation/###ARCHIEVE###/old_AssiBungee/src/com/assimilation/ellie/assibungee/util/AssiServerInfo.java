package com.assimilation.ellie.assibungee.util;

import com.assimilation.ellie.assibungee.server.ServerState;
import com.assimilation.ellie.assibungee.server.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiServerInfo {

    private String id;
    private ServerType serverType;
    private ServerState serverState;

    public AssiServerInfo(String id, ServerType serverType, ServerState serverState){
        this.id = id;
        this.serverType = serverType;
        this.serverState = serverState;
    }

    public String getID() {
        return id;
    }

    public ServerInfo getServer(){
        return ProxyServer.getInstance().getServerInfo(id);
    }

    public ServerType getServerType() {
        return serverType;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    @Override
    public String toString() {
        return id+";#;"+serverType.toString()+";#;"+serverState.toString();
    }

    public AssiServerInfo(String serialised){
        String[] args = serialised.split(";#;");
        if(args.length != 3){
            System.out.println("Invalid server info: "+serialised);
            return;
        }
        this.id = args[0];
        this.serverType = ServerType.valueOf(args[1]);
        this.serverState = ServerState.valueOf(args[2]);
    }
}
