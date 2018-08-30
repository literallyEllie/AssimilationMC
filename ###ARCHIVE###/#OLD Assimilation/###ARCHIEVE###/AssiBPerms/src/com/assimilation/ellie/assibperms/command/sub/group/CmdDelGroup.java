package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdDelGroup extends SubCommand {

     public CmdDelGroup(){
         super("delgroup", CmdPermLib.DEL_GROUP, "delgroup <group>", "Deletes a group");
     }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 2){

            if(!AssiBPerms.getAssiBPerms().getGroupManager().isGroup(args[1])){
                Util.mINFO(sender, "Group doesn't exist (at name) "+args[1]);
                return;
            }


            if(AssiBPerms.getAssiBPerms().getGroupManager().getDefaultGroup().getName().equalsIgnoreCase(args[1])){
                Util.mINFO(sender, "This group cannot be deleted as it is the default group");
                return;
            }

            AssiBPerms.getAssiBPerms().getGroupManager().deleteGroup(AssiBPerms.getAssiBPerms().getGroupManager().getGroup(args[1]));
            Util.mINFO(sender, "Group deleted with name of "+args[1]+".");

        }else{
            Util.mINFO_noP(sender, correctUsage());
            return;
        }
    }
}
