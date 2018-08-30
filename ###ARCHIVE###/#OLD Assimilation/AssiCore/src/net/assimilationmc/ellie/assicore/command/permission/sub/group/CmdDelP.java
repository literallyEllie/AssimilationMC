package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.permission.SpigotPermission;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdDelP extends SubCommand {

    public CmdDelP(){                   //     0     1            2                         3
        super("delp", PermissionLib.CMD.PERM.PERM_DEL, "delp <group> <permission>", "Removes a permission from a group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 3){
            sendPMessage(sender, correctUsage());
            return;
        }

        AssiPermGroup assiPermGroup = getPermissionManager().getGroup(args[1]);

        if(assiPermGroup == null){
            sendPMessage(sender, "Group doesn't exist (at name) "+ ColorChart.VARIABLE + args[1] + ColorChart.R + ".");
            return;
        }

        String permission = args[2];

        List<SpigotPermission> a = new ArrayList<>();
        assiPermGroup.getPermissions().forEach(spigotPermission -> {
            if(spigotPermission.getPermission().equalsIgnoreCase(permission)) a.add(spigotPermission);
        });
        assiPermGroup.getPermissions().removeAll(a);

        sendPMessage(sender, "Removed permission "+ ColorChart.VARIABLE + permission+ ColorChart.R + " from group "+ ColorChart.VARIABLE + assiPermGroup.getName()+
                ColorChart.R + " Could take up to 10 minutes to have effect.");
        getPermissionManager().setUpdate(true);
    }

}
