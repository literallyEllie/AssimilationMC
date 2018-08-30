package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.util.MessageLib;
import com.assimilation.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdFly extends AssiCommand {

    public CmdFly(){
        super("fly", PermissionLib.CMD.FLY, "fly [player]", "Toggle fly mode");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player targetPlayer = null;

        if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.FLY_OTHER)){

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

            boolean canFly = targetPlayer.getAllowFlight();

            if(canFly){
                targetPlayer.setFlying(false);
                targetPlayer.setAllowFlight(false);
            }else{
                targetPlayer.setAllowFlight(true);
                targetPlayer.setFlying(true);

            }

            sendPMessage(sender, "Set &9"+ targetPlayer.getName() +"&f's fly mode to "+!canFly);
        }
        else{

            Player p = (Player) sender;
            boolean canFly = p.getAllowFlight();

            if(canFly){
                p.setFlying(false);
                p.setAllowFlight(false);
            }else{
                p.setAllowFlight(true);
                p.setFlying(true);
            }

            sendPMessage(sender, "Set &9"+ sender.getName() +"&f's fly mode to "+!canFly);
        }

    }
}
