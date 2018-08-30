package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdGamemode extends AssiCommand {

    public CmdGamemode(){
        super("gamemode", PermissionLib.CMD.GAMEMODE, "gamemode <survival | creative | adventure | spectator> [player]", "Switch between gamemodes", Collections.singletonList("gm"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        GameMode gameMode = null;
        Player targetPlayer = null;

        if(args.length == 2 && sender.hasPermission(PermissionLib.CMD.GAMEMODE_OTHER)){

            targetPlayer = Bukkit.getPlayer(args[1]);

            if(targetPlayer == null){
                sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
            gameMode = parse(args[0].toLowerCase());
        }

        else if(args.length == 1 && sender instanceof Player){
        }
        else{
            sendMessage(sender, correctUsage());
            return;
        }

        if(gameMode == null) {
            gameMode = parse(args[0].toLowerCase());
        }

        if(targetPlayer != null){
            targetPlayer.setGameMode(gameMode);
            sendPMessage(sender, "Set "+ ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(gameMode.name().toLowerCase())+ColorChart.R+".");
        }
        else{
            ((Player) sender).setGameMode(gameMode);
            sendPMessage(sender, "Set "+ ColorChart.VARIABLE + sender.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(gameMode.name().toLowerCase())+ColorChart.R+".");
        }
    }

    public static class CmdSurvival extends AssiCommand {

        public CmdSurvival(){
            super("gms", PermissionLib.CMD.GAMEMODE, "gms [player]", "Switch to survival gamemode", Arrays.asList("survival", "gm0"));
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {

            Player targetPlayer = null;

            if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.GAMEMODE_OTHER)){

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
                targetPlayer.setGameMode(GameMode.SURVIVAL);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.SURVIVAL.name().toLowerCase())+ColorChart.R+".");
            }
            else{
                ((Player) sender).setGameMode(GameMode.SURVIVAL);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + sender.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.SURVIVAL.name().toLowerCase())+ColorChart.R+".");
            }

        }
    }

    public static class CmdCreative extends AssiCommand {

        public CmdCreative(){
            super("gmc", PermissionLib.CMD.GAMEMODE, "gmc [player]", "Switch to creative gamemode", Arrays.asList("creative", "gm1"));
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {

            Player targetPlayer = null;

            if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.GAMEMODE_OTHER)){

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
                targetPlayer.setGameMode(GameMode.CREATIVE);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.CREATIVE.name().toLowerCase())+ColorChart.R+".");
            }
            else{
                ((Player) sender).setGameMode(GameMode.CREATIVE);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + sender.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.CREATIVE.name().toLowerCase())+ColorChart.R+".");
            }

        }
    }

    public static class CmdAdventure extends AssiCommand {

        public CmdAdventure(){
            super("gma", PermissionLib.CMD.GAMEMODE, "gma [player]", "Switch to adventure gamemode", Arrays.asList("adventure", "gm2"));
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {

            Player targetPlayer = null;

            if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.GAMEMODE_OTHER)){

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
                targetPlayer.setGameMode(GameMode.ADVENTURE);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.ADVENTURE.name().toLowerCase())+ColorChart.R+".");
            }
            else{
                ((Player) sender).setGameMode(GameMode.ADVENTURE);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + sender.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.ADVENTURE.name().toLowerCase())+ColorChart.R+".");
            }


        }
    }

    public static class CadSpectator extends AssiCommand {

        public CadSpectator(){
            super("gmspec", PermissionLib.CMD.GAMEMODE, "gmspec [player]", "Switch to spectator gamemode", Arrays.asList("specator", "gm3"));
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {

            Player targetPlayer = null;

            if(args.length == 1 && sender.hasPermission(PermissionLib.CMD.GAMEMODE_OTHER)){

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
                targetPlayer.setGameMode(GameMode.SPECTATOR);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.SPECTATOR.name().toLowerCase())+ColorChart.R+".");
            }
            else{
                ((Player) sender).setGameMode(GameMode.SPECTATOR);
                sendPMessage(sender, "Set "+ ColorChart.VARIABLE + sender.getName() + ColorChart.R + "'s gamemode to "+ ColorChart.VARIABLE + StringUtils.capitalize(GameMode.SPECTATOR.name().toLowerCase())+ColorChart.R+".");
            }

        }
    }

    private GameMode parse(String arg){

        List<String> survival = Arrays.asList("survival", "s", "0");
        List<String> creative = Arrays.asList("creative", "c", "1");
        List<String> adventure = Arrays.asList("adventure", "a", "2");
        List<String> spectator = Arrays.asList("spectator", "spec", "3");

        if(survival.contains(arg)) return GameMode.SURVIVAL;
        if(creative.contains(arg)) return GameMode.CREATIVE;
        if(adventure.contains(arg)) return GameMode.ADVENTURE;
        if(spectator.contains(arg)) return GameMode.SPECTATOR;

        return GameMode.SURVIVAL;
    }


}
