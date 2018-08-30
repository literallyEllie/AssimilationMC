package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.event.ScoreboardUpdateEvent;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdVanish extends AssiCommand {

    public CmdVanish(){
        super("vanish", PermissionLib.CMD.VANISH, "vanish", "Hide yourself from regular players", Collections.singletonList("v"));
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
            Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(p.getName(), ScoreboardUpdateEvent.UpdateElement.ONLINE));
            unVanish(p);
        }else{
            sendPMessage(sender, "You have been vanished from regular players.");
            getCore().getVanishedPlayers().add(p.getUniqueId());
            Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(p.getName(), ScoreboardUpdateEvent.UpdateElement.ONLINE));
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

    public static void unVanish(Player player){
        Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(player));
    }



}
