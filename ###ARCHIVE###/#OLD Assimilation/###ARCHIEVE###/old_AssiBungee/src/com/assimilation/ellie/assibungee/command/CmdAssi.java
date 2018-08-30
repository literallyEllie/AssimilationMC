package com.assimilation.ellie.assibungee.command;

import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

/**
 * Created by Ellie on 22/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdAssi extends Command {

    public CmdAssi(){
        super("bassi");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(PermissionLib.CMD.ASSI)){
            Util.mWARN(sender, MessageLib.NO_PERMISSION);
            return;
        }

        if(args.length == 0){
            Util.mINFO(sender, String.format(MessageLib.CORRECT_USAGE, "assi <rlcfg>", "Reloads config"));
            return;
        }

        if(args[0].equalsIgnoreCase("rlcfg")){

            try {
                ModuleManager.getModuleManager().getBroadcastManager().assign(false);
            }catch(IOException e){
                e.printStackTrace();
                Util.mWARN(sender, "Failed to reload Broadcast config");
                return;
            }

            Util.mINFO(sender, "Configs reloaded.");

            return;
        }else{
            Util.mINFO(sender, MessageLib.INVALID_SUB_CMD);
            return;
        }


    }
}
