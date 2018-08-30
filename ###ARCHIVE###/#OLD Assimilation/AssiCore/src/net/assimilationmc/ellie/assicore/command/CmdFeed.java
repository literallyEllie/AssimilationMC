package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdFeed extends AssiCommand {

    public CmdFeed(){
        super("feed", PermissionLib.CMD.FEED, "feed [player]", "Feed your face");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player targetPlayer = null;

        if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.FEED_OTHER)){

            targetPlayer = Bukkit.getPlayer(args[0]);

            if(targetPlayer == null){
                sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
        }
        else if(args.length == 0 && sender instanceof Player){
        }
        else{
            sendMessage(sender, correctUsage());
            return;
        }

        if(targetPlayer != null){
            targetPlayer.setFoodLevel(20);
            sendPMessage(sender, "Fed "+ ColorChart.VARIABLE+ targetPlayer.getName() + ColorChart.R+".");
        }
        else{
            ((Player) sender).setFoodLevel(20);
            sendPMessage(sender, "Fed "+ ColorChart.VARIABLE+ sender.getName() + ColorChart.R+".");
        }


    }
}
