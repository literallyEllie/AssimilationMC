package com.assimilation.ellie.assibperms.command.sub;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

/**
 * Created by Ellie on 16/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdRefresh extends SubCommand {

    public CmdRefresh(){
        super("refresh", CmdPermLib.REFRESH, "refresh", "Runs update task, use in moderation and only when necessary");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Util.mINFO(sender, "Attempting to refresh permissions async... &lDo not spam this command");
        ProxyServer.getInstance().getScheduler().runAsync(AssiBPerms.getAssiBPerms(), AssiBPerms.getAssiBPerms().getGroupManager().getGroupSyncTask());
        Util.mINFO(sender, "Refreshed.");

    }

}
