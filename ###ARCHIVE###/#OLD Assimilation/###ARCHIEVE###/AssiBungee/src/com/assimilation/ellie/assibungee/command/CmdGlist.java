package com.assimilation.ellie.assibungee.command;

import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.manager.ServerManager;
import com.assimilation.ellie.assibungee.server.ServerType;
import com.assimilation.ellie.assibungee.util.AssiServerInfo;
import com.assimilation.ellie.assibungee.util.Util;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdGlist extends Command {

    public CmdGlist(){
        super("glist");
    }

    @Override
    public void execute(CommandSender s, String[] args) {

        ServerManager serverManager = ModuleManager.getModuleManager().getServerManager();

        List<ServerInfo> lobbies = serverManager.getServers().values().stream().filter(serverInfo -> serverInfo.getServerType().equals(ServerType.LOBBY)).
                map(AssiServerInfo::getServer).collect(Collectors.toList());
        int online = 0;
        for (ServerInfo lobby : lobbies) {
            online = online + lobby.getPlayers().size();
        }
        
        Util.mINFO(s, "&aOnline players: &9("+ ProxyServer.getInstance().getOnlineCount()+")");
        Util.mINFO_noP(s, "&2Lobbies &9("+online+")&a: "+ Joiner.on(", ").join(lobbies));


        serverManager.getServers().values().forEach(serverInfo -> {


        });

    }
}
