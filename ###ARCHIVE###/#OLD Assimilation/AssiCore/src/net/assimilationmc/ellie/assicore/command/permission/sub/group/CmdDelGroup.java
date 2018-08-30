package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdDelGroup extends SubCommand {

     public CmdDelGroup(){
         super("delgroup", PermissionLib.CMD.PERM.DEL_GROUP, "delgroup <group>", "Deletes a group");
     }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 2){

            if(!getPermissionManager().isGroup(args[1])){
                sendPMessage(sender, "Group doesn't exist (at name) "+ ColorChart.VARIABLE + args[1]+ ColorChart.R + ".");
                return;
            }


            try {
                if (getPermissionManager().getDefaultGroup().getName().equalsIgnoreCase(args[1])) {
                    sendPMessage(sender, "This group cannot be deleted as it is the default group.");
                    return;
                }
            }catch(IndexOutOfBoundsException ignored){}

            getPermissionManager().deleteGroup(getPermissionManager().getGroup(args[1]));
            sendPMessage(sender, "Group deleted with name of "+ ColorChart.VARIABLE + args[1]+ ColorChart.R + ".");

        }else{
            sendPMessage(sender, correctUsage());
        }
    }
}
