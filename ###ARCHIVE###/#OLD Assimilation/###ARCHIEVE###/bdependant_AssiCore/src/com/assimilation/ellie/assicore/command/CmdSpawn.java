package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.api.SerializableLocation;
import com.assimilation.ellie.assicore.util.MessageLib;
import com.assimilation.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSpawn extends AssiCommand {

    public CmdSpawn(){
        super("spawn", "spawn [player]", "Teleport to spawn", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        SerializableLocation spawn = getCore().getModuleManager().getConfigManager().getSpawn();

        if(spawn == null){
            sendPMessage(sender, "&cSpawn is undefined.");
            return;
        }

        Player targetPlayer = null;

        if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.SPAWN_OTHER)){

            targetPlayer = Bukkit.getPlayer(args[0]);

            if(targetPlayer == null){
                sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
        }
        else if(args.length == 0 && sender instanceof Player){
        }
        else{
            sendMessage(sender, correctUsage());
            return;
        }

        if(targetPlayer != null){
            targetPlayer.teleport(spawn.toLocation());
            sendPMessage(sender, "Sent &9"+ targetPlayer.getName() +"&f to spawn.");
        }
        else{
            ((Player) sender).teleport(spawn.toLocation());
            sendPMessage(sender, "You have been teleported to spawn.");
        }

    }

    public static class CmdSetSpawn extends AssiCommand {

        public CmdSetSpawn(){
            super("setspawn", PermissionLib.CMD.SET_SPAWN, "setspawn", "Set the spawn");
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {

            if(sender instanceof Player){

                Player player = (Player) sender;

                getCore().getModuleManager().getConfigManager().setSpawn(new SerializableLocation(player.getLocation()));
                sendPMessage(sender, "Set spawn to your location.");

            }else{
                sendPMessage(sender, MessageLib.NO_CONSOLE);
            }

        }
    }

}
