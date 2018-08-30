package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.permission.SpigotPermission;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdInfo extends SubCommand {

    //args.length:                          0      1      2        3         4
    public CmdInfo(){                           // 0      1        2         3
        super("info", PermissionLib.CMD.PERM.GROUP_INFO, "info <group> [worlds | *]", "Gives information about said group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {


        if(args.length > 1 && args.length < 5) {

            AssiPermGroup group = getPermissionManager().getGroup(args[1]);

            if (group == null) {
                sendPMessage(sender, "Group doesn't exist (at group) " + ColorChart.VARIABLE +  args[1] + ColorChart.R + ".");
                return;
            }

            if(args.length != 2) {

                if (args.length > 3) {
                    List<String> perm = new ArrayList<>();
                    String world;

                    if (args.length == 4) {
                        world = args[3];
                        sendPMessage(sender, "Permissions of " + ColorChart.VARIABLE + group.getName() + ColorChart.R + " in world: " + ColorChart.VARIABLE + world + "\n");
                        perm.addAll(group.getPermissions().stream().filter(spigotPermission -> spigotPermission.onWorld(world))
                                .map(SpigotPermission::getPermission).collect(Collectors.toSet()));
                        if (!perm.isEmpty()) {
                            sendMessage(sender, ColorChart.VARIABLE + Joiner.on(ColorChart.R + ", " + ColorChart.VARIABLE).join(perm));
                        }
                    }
                } else {
                    sendPMessage(sender, "All Permissions of " + ColorChart.VARIABLE + group.getName() + ColorChart.R + "\n");
                    List<String> perm = new ArrayList<>();

                    group.getPermissions().forEach(spigotPermission -> perm.add(spigotPermission.getPermission()));
                    sendMessage(sender, ColorChart.VARIABLE + Joiner.on(ColorChart.R+", "+ColorChart.VARIABLE).join(perm));
                }
                return;
            }

            sendMessage(sender, "Information about group " + ColorChart.VARIABLE + group.getName() + "\n");
            sendMessage(sender, "Prefix: '"+ColorChart.VARIABLE+ ColorChart.R + "' " + group.getPrefix().replace("&", "@") + ColorChart.R + "'");
            sendMessage(sender, "Suffix: '"+ ColorChart.VARIABLE + group.getSuffix().replace("&", "@") + ColorChart.R + "'");
            sendMessage(sender, "Parents: " + ColorChart.VARIABLE + group.getParents());
            sendMessage(sender, "Options: " + ColorChart.VARIABLE + group.getOptions());
            sendMessage(sender, "Permission count: " + ColorChart.VARIABLE + group.getPermissions().size());
            sendMessage(sender, "\nDo "+ColorChart.VARIABLE + "/permission info " + group.getName() + ColorChart.R + " to see all the specific permissions.");
        }
        else{
            sendPMessage(sender, correctUsage());
        }

    }
}
