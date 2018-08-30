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
            //
        }
        else{
            sendMessage(sender, correctUsage());
            return;
        }


        if(targetPlayer != null){

            boolean canFly = targetPlayer.getAllowFlight();
            targetPlayer.setAllowFlight(!canFly);
            targetPlayer.setFlying(!canFly);

            sendPMessage(sender, "Set "+ ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + "'s fly mode to "+ColorChart.VARIABLE+!canFly + ColorChart.R + ".");
        }
        else{

            Player p = (Player) sender;
            final boolean canFly = p.getAllowFlight();
            p.setAllowFlight(!canFly);
            p.setFlying(!canFly);

            sendPMessage(sender, "Set "+ ColorChart.VARIABLE + sender.getName() + ColorChart.R + "'s fly mode to "+ColorChart.VARIABLE+!canFly + ColorChart.R +".");
        }

    }
}
