package com.assimilation.ellie.assibperms.command;

import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdPermission extends Command {

    private PCommandManager commandManager;

    public CmdPermission(PCommandManager commandManager){
        super("permission");
        this.commandManager = commandManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!sender.hasPermission(CmdPermLib.MAIN)){
            Util.mWARN(sender, MessageLib.NO_PERMISSION);
            return;
        }

        if(args.length == 0){
            Util.mINFO(sender, String.format(MessageLib.CORRECT_USAGE, "permission help", "Permission Management"));
        }

        else{
            if(!commandManager.isCommand(args[0])){
                Util.mWARN(sender, "Invalid sub-command");
                return;
            }
            commandManager.getCommand(args[0]).execute(sender, args);
        }

    }
}
