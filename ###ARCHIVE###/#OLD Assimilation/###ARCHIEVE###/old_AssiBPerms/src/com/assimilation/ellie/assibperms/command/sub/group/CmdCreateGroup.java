package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

import java.util.HashSet;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdCreateGroup extends SubCommand {

    public CmdCreateGroup(){                          //0   1        2      3
        super("creategroup", CmdPermLib.CREATE_GROUP, "creategroup <name> [parent]", "Create a permission group for Proxy and Spigot");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        AssiPermGroup parent = null;

        if(args.length == 3){

            parent = AssiBPerms.getAssiBPerms().getGroupManager().getGroup(args[2]);

            if(parent == null){
                Util.mINFO(sender, "Invalid group (at parent) "+args[2]);
                return;
            }

        }else if(args.length == 2){

            if(AssiBPerms.getAssiBPerms().getGroupManager().isGroup(args[1])){
                Util.mINFO(sender, "Group already exists (at name) "+args[1]);
                return;
            }


            HashSet<String> parents = new HashSet<>();
            if(parent != null) parents.add(parent.getName());
            AssiPermGroup permGroup = new AssiPermGroup(args[1], parents);

            AssiBPerms.getAssiBPerms().getGroupManager().createGroup(permGroup);
            Util.mINFO(sender, "Group created with name of "+args[1]+".");

        }else{
            Util.mINFO_noP(sender, correctUsage());
        }

    }
}
