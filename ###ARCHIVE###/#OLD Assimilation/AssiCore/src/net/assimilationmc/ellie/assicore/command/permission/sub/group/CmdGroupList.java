package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdGroupList extends SubCommand {

    public CmdGroupList(){                          //0   1        2      3
        super("list", PermissionLib.CMD.PERM.LIST_GROUP, "list", "Lists all the groups");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        sendPMessage(sender, "All permission groups");
        getPermissionManager().getGroups().forEach((s, permGroup) -> Util.mINFO_noP(sender, ColorChart.R + permGroup.getName()+" "+ ColorChart.VARIABLE + permGroup.getParents()));
    }

}
