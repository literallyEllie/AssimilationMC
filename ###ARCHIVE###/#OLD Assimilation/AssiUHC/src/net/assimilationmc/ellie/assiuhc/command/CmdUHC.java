package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assicore.command.AssiCommand;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdUHC extends AssiCommand {

    public CmdUHC(){
        super("uhc", "uhc help", "Commands for UHC", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            SubCommand command = UHC.getPlugin(UHC.class).getCommandManager().getCommand(args[0]);
            if (command != null && sender.hasPermission(command.getBasePerm())) {
                command.onCommand(sender, args);
                return;
            }
        }

        sender.sendMessage(Util.color(UHC.prefix + "Help for AssimilationUHC:") + "\n");
        UHC.getPlugin(UHC.class).getCommandManager().getCommands().values().forEach(subCommand -> {
            if (sender.hasPermission(subCommand.getBasePerm()))
                sendMessage(sender, UColorChart.COMMAND_USAGE + "/" + subCommand.getUsage() + UColorChart.R + " - " + UColorChart.COMMAND_DESC + subCommand.getDescription() + UColorChart.R + ".");
        });

        sender.sendMessage(Util.color(UColorChart.R + "\n---------------------------"));
    }

}
