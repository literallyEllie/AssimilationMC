package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.permission.SpigotPermission;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdDelP extends SubCommand {

    public CmdDelP(){                   //     0     1            2                         3
        super("delp", CmdPermLib.PERM_DEL, "delp <group> <b | s> (bungee | spigot) <permission>", "Removes a permission from a group on spigot/bungee system");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 4){
            Util.mINFO_noP(sender, correctUsage());
            return;
        }

        AssiPermGroup assiPermGroup = AssiBPerms.getAssiBPerms().getGroupManager().getGroup(args[1]);

        if(assiPermGroup == null){
            Util.mINFO(sender, "Group doesn't exist (at name) "+args[1]);
            return;
        }

        String system = args[2];
        String permission = args[3];

        switch (system.toLowerCase()){
            case "b":
                assiPermGroup.getPermissions().remove(permission);
                break;
            case "s":
                List<SpigotPermission> a = new ArrayList<>();
                assiPermGroup.getSpigotPermissions().forEach(spigotPermission -> {
                    if(spigotPermission.getPermission().equalsIgnoreCase(permission)) a.add(spigotPermission);
                });
                assiPermGroup.getSpigotPermissions().removeAll(a);
                System.out.println("removed "+a);
                break;
            default:
                Util.mINFO(sender, "Invalid system (at system) "+system);
                return;
        }

        Util.mINFO(sender, "Removed permission &9"+permission+" &ffrom group &9"+assiPermGroup.getName()+" ("+system+") &fCould take up to 10 minutes to have effect");
        AssiBPerms.getAssiBPerms().getGroupManager().setUpdate(true);
    }

}
