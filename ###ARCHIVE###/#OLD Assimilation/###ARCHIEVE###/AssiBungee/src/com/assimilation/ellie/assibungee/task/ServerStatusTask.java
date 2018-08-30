package com.assimilation.ellie.assibungee.task;

import com.assimilation.ellie.assibungee.AssiBungee;
import com.assimilation.ellie.assibungee.manager.ServerManager;
import com.assimilation.ellie.assibungee.server.ServerPing;
import com.assimilation.ellie.assibungee.server.ServerState;
import com.assimilation.ellie.assibungee.util.AssiServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ServerStatusTask implements Runnable {

    private LinkedHashMap<String, AssiServerInfo> map;
    private int timeout;

    private List<ServerManager> callback = new ArrayList<>();

    public ServerStatusTask(ServerManager serverManager, LinkedHashMap<String, AssiServerInfo> map) {
        this(serverManager, map, 5);
    }

    public ServerStatusTask(ServerManager serverManager, LinkedHashMap<String, AssiServerInfo> map, int timeout) {
        this.map = map;
        this.timeout = timeout;
        callback.add(serverManager);
    }

    @Override
    public void run() {

        AssiBungee.getAssiBungee().logW("Pinging servers...");
        map.forEach((s, serverInfo) -> {

            try {

                new ServerPing().getPing(new ServerPing.Options().setHostname(serverInfo.getServer().getAddress().getHostName()).setPort(serverInfo.getServer()
                    .getAddress().getPort()).setTimeout(timeout));

                AssiBungee.getAssiBungee().logW("Server "+s+" responded successfully in "+timeout+"ms");


            }catch(IOException e){
                AssiBungee.getAssiBungee().logW("Failed to ping server "+s+": "+e.getLocalizedMessage());
                serverInfo.setServerState(ServerState.ERROR);
                AssiBungee.getAssiBungee().logE("Server "+s+ " did not respond in "+timeout+" ms!");
                this.callback.forEach(serverManager -> serverManager.run(map));
            }

        });


    }
}
