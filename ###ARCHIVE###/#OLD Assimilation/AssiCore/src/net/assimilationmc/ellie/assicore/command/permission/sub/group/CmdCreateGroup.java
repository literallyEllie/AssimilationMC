package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdCreateGroup extends SubCommand {

    public CmdCreateGroup(){                          //0   1        2      3
        super("creategroup", PermissionLib.CMD.PERM.CREATE_GROUP, "creategroup <name> [parent...]", "Create a permission group for Spigot");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        List<String> parent = new ArrayList<>();

        if(args.length > 2){

            String[] groups = Util.getFinalArg(args, 2).split(" ");

            for (String s : groups) {
                if(getPermissionManager().getGroup(s) != null)
                    parent.add(getPermissionManager().getGroup(s).getName());
                else
                    sendPMessage(sender, "Invalid group (at parent) "+ ColorChart.VARIABLE + s + ColorChart.R +".");
            }

        }

        if(args.length < 2) {
            sendPMessage(sender, correctUsage());
            return;
        }

        if(getPermissionManager().isGroup(args[1])){
            sendPMessage(sender, "Group already exists (at name) "+ColorChart.VARIABLE + args[1]+ColorChart.R + ".");
            return;
        }

        AssiPermGroup permGroup = new AssiPermGroup(args[1], parent);

        getPermissionManager().createGroup(permGroup);
        sendPMessage(sender, "Group created with name of "+ ColorChart.VARIABLE + args[1]+ ColorChart.R + ".");


    }
}
