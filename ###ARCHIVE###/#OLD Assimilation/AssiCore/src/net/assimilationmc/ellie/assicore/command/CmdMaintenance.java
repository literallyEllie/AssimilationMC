package net.assimilationmc.ellie.assicore.command;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.api.ServerState;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 15/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdMaintenance extends AssiCommand {

    public CmdMaintenance(){
        super("maintenance", PermissionLib.CMD.MAINTENANCE, "maintenance <enable|disable|message|list|add|remove> [message|player]", "Maintenance mode options");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        ServerState serverState = getCore().getServerState();

        if(args.length == 1){

            switch(args[0].toLowerCase()) {
                case "enable":
                    if (serverState != ServerState.MAINTENANCE) {
                        getCore().setServerState(ServerState.MAINTENANCE);
                        getCore().logW(sender.getName() + " has enabled maintenance");
                        Bukkit.getOnlinePlayers().forEach(o -> {
                            if (!o.hasPermission(PermissionLib.BYPASS.MAINTENANCE)) {
                                Util.kickPlayer(o, "The server has now entered maintenance mode!\n\n&cWe will be back soon!");
                            } else {
                                sendPMessage(sender, ColorChart.WARN + "Maintenance mode has now been enabled.");
                            }
                        });
                        return;
                    }
                    sendPMessage(sender, "Maintenance mode is already enabled.");
                    return;
                case "disable":
                    if(serverState == ServerState.MAINTENANCE || serverState == ServerState.ERROR){
                        getCore().setServerState(ServerState.STABLE);
                        getCore().logW(sender.getName()+" has disabled maintenance");
                        Bukkit.getOnlinePlayers().forEach(o -> sendPMessage(sender, ColorChart.WARN + "Maintenance mode is no longer active. Players can now rejoin."));
                        return;
                    }
                    sendPMessage(sender, "Maintenance mode is not enabled.");
                    return;
                case "message":
                    sendPMessage(sender, "The current message is: "+ ColorChart.VARIABLE + ModuleManager.getModuleManager().getConfigManager().getMaintenanceReason());
                    sendMessage(sender, "To set a new message do "+ColorChart.VARIABLE + "/maintenance message <message>"+ColorChart.R+".");
                    return;
                case "list":
                    sendPMessage(sender, "People in the whitelist:");
                    sendMessage(sender, ColorChart.VARIABLE+Joiner.on(ColorChart.R+", "+ColorChart.VARIABLE).join(ModuleManager.getModuleManager().getConfigManager().getMaintenanceWhitelist()));
                    return;
            }
        }
        else if(args.length > 1){

            switch(args[0].toLowerCase()){
                case "message":
                    String message = Util.getFinalArg(args, 1);
                    ModuleManager.getModuleManager().getConfigManager().setMaintenanceReason(message);
                    sendPMessage(sender, "Set the message to {prefix}"+ColorChart.VARIABLE+message);
                    return;
                case "add":
                    ModuleManager.getModuleManager().getConfigManager().add(args[1]);
                    sendPMessage(sender, ColorChart.VARIABLE +args[1]+ ColorChart.R + "has been added to the whitelist.");
                    return;
                case "remove":
                    ModuleManager.getModuleManager().getConfigManager().remove(args[1]);
                    sendPMessage(sender, ColorChart.VARIABLE +args[1]+ ColorChart.R + " has been removed to the whitelist.");
                    return;
            }


            return;
        }
        sendMessage(sender, correctUsage());
        sendMessage(sender, "The server is currently in "+ ColorChart.VARIABLE + serverState.name() + ColorChart.R +".");

    }

}
