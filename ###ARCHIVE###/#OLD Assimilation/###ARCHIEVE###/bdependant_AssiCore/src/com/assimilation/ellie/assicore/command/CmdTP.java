package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.util.MessageLib;
import com.assimilation.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Ellie on 16/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdTP extends AssiCommand {

    public CmdTP(){
        super("tp", PermissionLib.CMD.TP, "tp <player> [player]", "Teleport to an online player", Arrays.asList("teleport"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player player;
        Player targetPlayer = null;

        if(args.length == 2 && sender.hasPermission(PermissionLib.CMD.TP_OTHER)){

            targetPlayer = Bukkit.getPlayer(args[1]);
            player = Bukkit.getPlayer(args[0]);

            if(targetPlayer == null || player == null){
                sendMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
        }
        else
        if(args.length == 1 && sender instanceof Player){

            player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sendMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
        }
        else{
            sendMessage(sender, correctUsage());
            return;
        }

        if(player != null && targetPlayer != null){
            player.teleport(targetPlayer);

            sendPMessage(sender, "Teleported &9"+ player.getName()+"&f to &9"+targetPlayer.getName());
        }
        else if(player != null && targetPlayer == null){

            ((Player) sender).teleport(player);
            sendPMessage(sender, "Teleported to &9"+player.getName());

        }

    }
}
