package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.permission.SpigotPermission;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdInfo extends SubCommand {

    //args.length:                      0      1     2        3                              4                5
    public CmdInfo(){                       // 0     1        2                              3                4
        super("info", CmdPermLib.GROUP_INFO, "info <group> [b | s] (bungee | spigot) <if s: [servers | *] [worlds | *]>", "Gives information about said group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {


        if(args.length > 1 && args.length < 6) {

            AssiPermGroup group = AssiBPerms.getAssiBPerms().getGroupManager().getGroup(args[1]);

            if (group == null) {
                Util.mINFO(sender, "Group doesn't exist (at group) " + args[1]);
                return;
            }



            if(args.length != 2) {
                String system = args[2];
                switch (system.toLowerCase()) {
                    case "b":
                        if (args.length == 3) {
                            Util.mINFO(sender, "Bungee permissions of &9" + group.getName() + "\n");
                            Util.mINFO_noP(sender, "&9" + Joiner.on("&f, &9").join(group.getPermissions()));
                            break;
                        }
                    case "s":
                        if (args.length > 3) {
                            List<String> perm = new ArrayList<>();
                            String server = args[3];
                            System.out.println(server);
                            String world;


                            if (args.length == 4) {
                                System.out.println("a");
                                Util.mINFO(sender, "Spigot permissions of &9" + group.getName() + " &fon server &9" + server + "\n");
                                perm.addAll(group.getSpigotPermissions().stream().filter(spigotPermission -> spigotPermission.onServer(server)).
                                        map(SpigotPermission::getPermission).collect(Collectors.toSet()));
                                if(!perm.isEmpty()) {
                                    Util.mINFO_noP(sender, "&9" + Joiner.on("&f, &9").join(perm));
                                }
                                break;
                            } else if(args.length == 5){
                                world = args[4];
                                System.out.println(world);
                                Util.mINFO(sender, "Spigot permissions of &9" + group.getName() + " &fon server &9" + server + " &fin world: &9" + world + "\n");
                                perm.addAll(group.getSpigotPermissions().stream().filter(spigotPermission -> spigotPermission.onServer(server) && spigotPermission.onWorld(world)).
                                        map(SpigotPermission::getPermission).collect(Collectors.toSet()));
                                if(!perm.isEmpty()) {
                                    Util.mINFO_noP(sender, "&9" + Joiner.on("&f, &9").join(perm));
                                }
                                break;
                            }
                        } else {
                            Util.mINFO(sender, "All Spigot permissions of &9" + group.getName() + "\n");
                            List<String> perm = new ArrayList<>();

                            group.getSpigotPermissions().forEach(spigotPermission -> perm.add(spigotPermission.getPermission()));
                            Util.mINFO_noP(sender, "&9" + Joiner.on("&f, &9").join(perm));
                        }
                        break;
                    default:
                        Util.mINFO(sender, "Invalid system (at system) " + system);
                        return;
                }
                return;
            }

            Util.mINFO(sender, "Information about group &9" + group.getName() + "\n");
            Util.mINFO_noP(sender, "Prefix: &9'" + group.getPrefix().replace("&", "@") + "'");
            Util.mINFO_noP(sender, "Suffix: &9'" + group.getSuffix().replace("&", "@") + "'");
            Util.mINFO_noP(sender, "Parents: &9" + group.getParents());
            Util.mINFO_noP(sender, "Options: &9" + group.getOptions());
            Util.mINFO_noP(sender, "Bungee Permission count: &9" + group.getPermissions().size());
            Util.mINFO_noP(sender, "Spigot Permission count: &9" + group.getSpigotPermissions().size());
            Util.mINFO_noP(sender, "\nDo &9/permission info " + group.getName() + " b | s &fto see all the specified system permissions.");
        }
        else{
            Util.mINFO_noP(sender, correctUsage());
        }

    }
}
