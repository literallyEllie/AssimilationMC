package net.assimilationmc.ellie.assicore.command.permission.sub;

import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.command.CommandSender;

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

        sendPMessage(sender, "All commands");
        getManager().getCommands().forEach((s, subCommand) -> Util.mINFO_noP(sender, ColorChart.R + "/"+ ColorChart.VARIABLE + subCommand.getUsage()+" "+ColorChart.R + " - " + ColorChart.VARIABLE +subCommand.getDescription()));

    }


}
