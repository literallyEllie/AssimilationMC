package com.assimilation.ellie.assibungee.command;

import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.server.ServerState;
import com.assimilation.ellie.assibungee.util.AssiServerInfo;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdMaintenance extends Command {

    public CmdMaintenance(){
        super("maintenance");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(PermissionLib.CMD.MAINTENANCE)){
            Util.mWARN(sender, MessageLib.NO_PERMISSION);
            return;
        }

        if(args.length == 2){
            if(args[0].equalsIgnoreCase("toggle")){

                List<String> results = Util.filter(ModuleManager.getModuleManager().getServerManager().getServers().keySet(), args[1]);

                if(results.size() > 1){
                    Util.mINFO(sender, "There was more than 1 result for your query. Please make it more specific.");
                    return;
                }

                if(results.size() == 0){
                    Util.mINFO(sender, "There were no results for your query.");
                    return;
                }

                AssiServerInfo info = ModuleManager.getModuleManager().getServerManager().getServer(results.get(0));


                if(info.getServerState().equals(ServerState.STABLE)){
                    Util.mWARN(sender, "Sending the server "+results.get(0)+" into maintenance mode now");
                    ModuleManager.getModuleManager().getServerManager().updateServer(results.get(0), ServerState.MAINTENANCE);
                    ProxyServer.getInstance().getServerInfo(results.get(0)).getPlayers().forEach(proxiedPlayer -> {
                        if(!proxiedPlayer.hasPermission(PermissionLib.BYPASS.MAINTENANCE)){
                            ModuleManager.getModuleManager().getServerManager().sendLobby(proxiedPlayer);
                        }
                    });
                }else{
                    Util.mWARN(sender, "Taking the server "+results.get(0)+" out of maintenance mode now");
                    ModuleManager.getModuleManager().getServerManager().updateServer(results.get(0), ServerState.STABLE);
                }


                return;
            }
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("list-all")){
                Util.mINFO(sender, "All Servers:");
                ModuleManager.getModuleManager().getServerManager().getServers().forEach((s, server) -> Util.mINFO_noP(sender, s+"&7: &9"+server.getServerState()));
                return;
            }

            else if(args[0].equalsIgnoreCase("list-main")){
                Util.mINFO(sender, "Servers in maintenance mode:");
                ModuleManager.getModuleManager().getServerManager().getServers().forEach((s, server) -> {
                    if(server.getServerState().equals(ServerState.MAINTENANCE)) {
                        Util.mINFO_noP(sender, s + "&7: &9" + server.getServerState());
                    }
                });
                return;
            }
        }


        Util.mINFO(sender, String.format(MessageLib.CORRECT_USAGE, "maintenance list-all | list-main (maintenance) | toggle <server>", "Toggle maintenance mode for a server."));
        return;

    }

}
