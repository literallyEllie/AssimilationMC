package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;

/**
 * Created by Ellie on 15/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdAssi extends AssiCommand {

    public CmdAssi(){
        super("assi", PermissionLib.CMD.ASSI, "assi refresh-perms | rlcfg", "Internal commands for AssiCore");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 1){
            sendMessage(sender, correctUsage());
            return;
        }

        if(args[0].equalsIgnoreCase("refresh-perms")){
            sendPMessage(sender, "Attempting to refresh permissions async... &lDo not spam this command");
            Bukkit.getScheduler().runTaskAsynchronously(getCore().getAssiPlugin(), getCore().getModuleManager().getPermissionManager().getGroupSyncTask());
            sendPMessage(sender, "Refreshed.");
        }

        if(args[0].equalsIgnoreCase("rlcfg")){
            sendPMessage(sender, "Reloading the config..");
            try {
                ModuleManager.getModuleManager().getConfigManager().assign(false);
            }catch(IOException e){
                sendPMessage(sender, "&cFailed to reload the config.");
                e.printStackTrace();
                return;
            }
            sendPMessage(sender, "Reloaded the config");
        }

    }


}
