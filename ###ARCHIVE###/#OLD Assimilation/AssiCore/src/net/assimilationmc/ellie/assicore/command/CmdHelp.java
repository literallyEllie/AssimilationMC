package net.assimilationmc.ellie.assicore.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdHelp extends AssiCommand {

    public CmdHelp(){
        super("help", "/help", "Help menu for the server", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] args) {
        getCore().getModuleManager().getConfigManager().getHelpFile().getHelp().forEach(s -> sendMessage(commandSender, s));
    }

}
