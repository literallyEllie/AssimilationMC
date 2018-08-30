package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdMessage extends AssiCommand implements TabExecutor {

    static HashMap<String, String> message_map = new HashMap<>();

    public CmdMessage(){
        super("message", "", "message <player> <message>", "Message an online player.", Arrays.asList("m", "msg", "t", "tell"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2){
            sendMessage(sender, String.format(MessageLib.CORRECT_USAGE, "message <player> <message>", "Message an online player."));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null){
            sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
            return;
        }

        message_map.put(player.getName(), sender.getName());

        String message = Util.getFinalArg(args, 1);

        // target
        if(isPlayer(sender)) {
            ((Player)sender).spigot().sendMessage(new ComponentBuilder("You").color(ChatColor.GREEN).bold(true).append(" -> ").color(ChatColor.DARK_GRAY).bold(false).append(player.getName()).color(ChatColor.GREEN)
                    .append(": ").color(ChatColor.DARK_GRAY).append(message).color(ChatColor.WHITE).create());
        }else{
            sender.sendMessage("You -> "+player.getName()+": "+message);
        }
        player.spigot().sendMessage(new ComponentBuilder(sender.getName()).color(ChatColor.GREEN).append(" -> ").color(ChatColor.DARK_GRAY).append("You").color(ChatColor.GREEN).bold(true)
                .append(": ").color(ChatColor.DARK_GRAY).bold(false).append(message).color(ChatColor.WHITE).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to reply to this message.").color(ChatColor.GRAY).create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r ")).create());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if(args.length != 1){
            return new ArrayList<>();
        }

        List<String> matches = new ArrayList<>();

        String query = args[0].toLowerCase();

        for (Player players: Bukkit.getOnlinePlayers()) {
            if (players.getName().toLowerCase().startsWith(query)) {
                matches.add(players.getName());
            }
        }

        return matches;
    }


}
