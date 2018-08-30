package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdVanish extends AssiCommand {

    public CmdVanish(){
        super("vanish", PermissionLib.CMD.VANISH, "/vanish", "Hide yourself from regular players", Arrays.asList("v"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(!isPlayer(sender)){
            return;
        }

        Player p = (Player )sender;

        if(getCore().getVanishedPlayers().contains(p.getUniqueId())){
            sendPMessage(sender, "You have been unvanished.");
            getCore().getVanishedPlayers().remove(p.getUniqueId());
            unvanish(p);
        }else{
            sendPMessage(sender, "You have been vanished from regular players");
            getCore().getVanishedPlayers().add(p.getUniqueId());
            vanish(p);
        }

    }

    public static void vanish(Player player){
        Bukkit.getOnlinePlayers().forEach(o -> {
            if(!o.hasPermission(PermissionLib.BYPASS.VANISH)) {
                o.hidePlayer(player);
            }
        });
    }

    public static void unvanish(Player player){
        Bukkit.getOnlinePlayers().forEach(o -> {
            o.showPlayer(player);
        });
    }



}
