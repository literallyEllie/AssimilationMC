package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.util.MessageLib;
import com.assimilation.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdClearInventory extends AssiCommand {

    public CmdClearInventory(){
        super("clearinventory", PermissionLib.CMD.CLEARINVENTORY, "clearinventory [player]", "Clear an inventory", Arrays.asList("ci"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player targetPlayer = null;

        if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.CLEARINVENTORY_OTHER)){

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
            targetPlayer.getInventory().clear();
            targetPlayer.getInventory().setArmorContents(null);
            sendPMessage(sender, "Cleared &9"+ targetPlayer.getName() +"&f's inventory");
        }
        else{
            ((Player) sender).getInventory().clear();
            ((Player) sender).getInventory().setArmorContents(null);
            sendPMessage(sender, "Cleared &9"+ sender.getName() +"&f's inventory");
        }


    }
}
