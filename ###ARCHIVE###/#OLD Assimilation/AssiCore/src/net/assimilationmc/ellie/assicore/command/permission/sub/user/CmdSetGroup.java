package net.assimilationmc.ellie.assicore.command.permission.sub.user;

import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.event.ScoreboardUpdateEvent;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by Ellie on 16/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSetGroup extends SubCommand {

    public CmdSetGroup(){
        super("setgroup", PermissionLib.CMD.PERM.SET_GROUP, "setgroup <group> <player>", "Sets a player's group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 3){
            Util.mINFO(sender, correctUsage());
            return;
        }

        AssiPermGroup assiPermGroup = getPermissionManager().getGroup(args[1]);

        if(assiPermGroup == null){
            sendPMessage(sender, "Group doesn't exist (at group) "+ ColorChart.VARIABLE + args[1] + ColorChart.R + ".");
            return;
        }

        UUID player = ModuleManager.getModuleManager().getPlayerManager().getPlayer(args[2]);

        if(player == null){
            sendPMessage(sender, "Player doesn't exist (at player) "+ ColorChart.VARIABLE + args[2] + ColorChart.R +".");
            return;
        }

        ModuleManager.getModuleManager().getPlayerManager().setGroup(assiPermGroup, player);
        if(Bukkit.getPlayer(player) != null){
            Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(Bukkit.getPlayer(player).getName(), ScoreboardUpdateEvent.UpdateElement.RANK));
        }

        sendPMessage(sender, "Set player "+ ColorChart.VARIABLE + args[2]+ ColorChart.R + "'s group to "+ ColorChart.VARIABLE + assiPermGroup.getName()+ ColorChart.R + " Could take up to 10 minutes to have effect.");
        ModuleManager.getModuleManager().getPermissionManager().setUpdate(true);
    }

}
