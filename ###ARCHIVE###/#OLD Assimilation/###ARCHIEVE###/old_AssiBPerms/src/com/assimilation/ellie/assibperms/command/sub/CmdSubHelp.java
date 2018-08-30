package com.assimilation.ellie.assibperms.command.sub;

import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSubHelp extends SubCommand {

    public CmdSubHelp(){
        super("help", "", "help", "Shows all help");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Util.mINFO(sender, "All commands");
        getManager().getCommands().forEach((s, subCommand) -> Util.mINFO_noP(sender, "/"+subCommand.getUsage()+" &9- &7"+subCommand.getDescription()));

    }


}
