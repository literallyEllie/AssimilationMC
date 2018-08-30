package net.assimilationmc.assicore.player;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;

public class CmdWhereAmI extends AssiCommand {

    public CmdWhereAmI(AssiPlugin plugin) {
        super(plugin, "whereAmI", "A command to show you which server you are on, for reporting bugs and whatnot", Lists.newArrayList());
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        sender.sendMessage(prefix(usedLabel) + "Hello! You are currently on the server " + C.V + asPlayer(sender).getLastSeenServer() + C.C + ".");
    }

}
