package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdGroupList extends SubCommand {

    public CmdGroupList(){                          //0   1        2      3
        super("list", CmdPermLib.LIST_GROUP, "list", "Lists all the groups");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Util.mINFO(sender, "All permission groups");
        AssiBPerms.getAssiBPerms().getGroupManager().getLoadedGroups().forEach((s, permGroup) -> Util.mINFO_noP(sender, permGroup.getName()+" &9"+permGroup.getParents()));
    }

}
