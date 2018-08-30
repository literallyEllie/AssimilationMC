package com.assimilation.ellie.assibperms.command.sub.user;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;

/**
 * Created by Ellie on 16/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSetGroup extends SubCommand {

    public CmdSetGroup(){
        super("setgroup", CmdPermLib.SET_GROUP, "setgroup <group> <player>", "Sets a player's group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 3){
            Util.mINFO_noP(sender, correctUsage());
            return;
        }

        AssiPermGroup assiPermGroup = AssiBPerms.getAssiBPerms().getGroupManager().getGroup(args[1]);

        if(assiPermGroup == null){
            Util.mINFO(sender, "Group doesn't exist (at group) "+args[1]);
            return;
        }

        UUID player = ModuleManager.getModuleManager().getPlayerManager().getPlayer(args[2]);

        if(player == null){
            Util.mINFO(sender, "Player doesn't exist (at player) "+args[2]);
            return;
        }

        AssiBPerms.getAssiBPerms().getUserManager().setGroup(assiPermGroup, player);

        Util.mINFO(sender, "Set player &9"+args[2]+"&f's group to &9"+ assiPermGroup.getName()+" &fCould take up to 10 minutes to have effect");
        AssiBPerms.getAssiBPerms().getGroupManager().setUpdate(true);
    }

}
