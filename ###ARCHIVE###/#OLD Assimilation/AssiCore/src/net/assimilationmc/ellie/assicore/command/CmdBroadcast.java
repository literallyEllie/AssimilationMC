package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdBroadcast extends AssiCommand {

    public CmdBroadcast(){
        super("broadcast", PermissionLib.CMD.BROADCAST, "broadcast <message>", "Broadcast a message to the server", Arrays.asList("bc"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, correctUsage());
            return;
        }

        String message = Util.getFinalArg(args, 0);
        String alert = Util.color("&9Server-Alert &f" + message); // exception to color chart
        Bukkit.broadcastMessage(alert);
    }
}
