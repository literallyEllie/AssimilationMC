package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.permission.SpigotPermission;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdAddP extends SubCommand{

                        //args.length:  0      1     2       3                         4                     5                 6
    public CmdAddP(){                   //     0     1       2                         3                     4                 5
        super("addp", CmdPermLib.PERM_ADD, "addp <group> <b | s> (bungee | spigot) <permission> <if s: [servers... | *]; [worlds... | *]>", "Add a permission to a group on spigot/bungee system");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length < 4){
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
        SpigotPermission spigotPermission = null;
        Set<String> servers = new HashSet<>();
        Set<String> worlds = new HashSet<>();

        switch (system.toLowerCase()){
            case "b":
                assiPermGroup.getPermissions().add(permission);
                break;
            case "s":

                spigotPermission = new SpigotPermission(permission);
                if(args.length > 4){

                    String serverWorlds = Util.getFinalArg(args, 4);
                    if(!serverWorlds.contains(";")){

                        servers.addAll(Arrays.asList(serverWorlds.split(" ")));
                        worlds.add("*");

                    }else {
                        servers.addAll(Arrays.asList(serverWorlds.split(";")[0].split(" ")));
                        worlds.addAll(Arrays.asList(serverWorlds.split(";")[1].split(" ")));
                    }
                }else{
                    servers.add("*");
                    worlds.add("*");
                }

                servers.removeAll(Arrays.asList(""));
                worlds.removeAll(Arrays.asList(""));

                if(servers.isEmpty()){
                    servers.add("*");
                }

                if(worlds.isEmpty()){
                    worlds.add("*");
                }


                spigotPermission.setServers(servers);
                spigotPermission.setWorlds(worlds);
                assiPermGroup.getSpigotPermissions().add(spigotPermission);
                break;
            default:
                Util.mINFO(sender, "Invalid system (at system) "+system);
                return;
        }

        if(spigotPermission != null){
            Util.mINFO(sender, "Added permission &9"+permission+" &fto group &9"+assiPermGroup.getName()+" ("+system+")&f In &9"+ Joiner.on(", ").join(servers)+"&f on worlds: &9"+Joiner.on(", ").join(worlds));
            Util.mINFO(sender, "&fCould take up to 10 minutes to have effect");
        }else
            Util.mINFO(sender, "Added permission &9"+permission+" &fto group &9"+assiPermGroup.getName()+" ("+system+") &fCould take up to 10 minutes to have effect");


        AssiBPerms.getAssiBPerms().getGroupManager().setUpdate(true);
    }
}
