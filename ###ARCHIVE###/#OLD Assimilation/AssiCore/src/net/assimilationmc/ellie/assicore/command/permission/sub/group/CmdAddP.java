package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.permission.SpigotPermission;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdAddP extends SubCommand {

                        //args.length:  0      1           2       3          4             5
    public CmdAddP(){                   //     0           1       2          3             4
        super("addp", PermissionLib.CMD.PERM.PERM_ADD, "addp <group> <permission> [worlds... | *]", "Add a permission to a group on the Spigot system");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            sendPMessage(sender, correctUsage());
            return;
        }

        AssiPermGroup assiPermGroup = getPermissionManager().getGroup(args[1]);

        if (assiPermGroup == null) {
            sendPMessage(sender, "Group doesn't exist (at name) " + ColorChart.VARIABLE + args[1] + ColorChart.R + ".");
            return;
        }

        String permission = args[2];
        SpigotPermission spigotPermission = new SpigotPermission(permission);
        Set<String> worlds = new HashSet<>();

        sendMessage(sender, "args.length " + args.length);
        if (args.length > 3) {

            String serverWorlds = Util.getFinalArg(args, 3);
            if (!serverWorlds.contains(";")) {
                worlds.add("*");
            } else worlds.addAll(Arrays.asList(serverWorlds.split(";")[1].split(" ")));

        } else
            worlds.add("*");

        worlds.removeAll(Collections.singletonList(""));

        if (worlds.isEmpty()) {
            worlds.add("*");
        }

        spigotPermission.setWorlds(worlds);
        assiPermGroup.getPermissions().add(spigotPermission);

        sendPMessage(sender, "Added permission " + ColorChart.VARIABLE + permission + ColorChart.R +
                " to group " + ColorChart.VARIABLE + assiPermGroup.getName() + ColorChart.R + " on worlds: " + ColorChart.VARIABLE + Joiner.on(", ").join(worlds) + ColorChart.R + ".");
        sendMessage(sender, "Could take up to 10 minutes to have effect.");

        getPermissionManager().setUpdate(true);
    }

}
