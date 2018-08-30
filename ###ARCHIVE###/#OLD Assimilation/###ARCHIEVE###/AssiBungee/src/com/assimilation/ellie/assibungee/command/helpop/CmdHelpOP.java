package com.assimilation.ellie.assibungee.command.helpop;

import com.assimilation.ellie.assibungee.manager.HelpOPManager;
import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.server.AssiPlayer;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.util.Set;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdHelpOP extends Command {

    HelpOPManager helpOPManager = ModuleManager.getModuleManager().getHelpOPManager();

    public CmdHelpOP(){
        super("helpop");
    }

    // /helpop [handle | info]

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ConsoleCommandSender){
            Util.mWARN(commandSender, MessageLib.NO_CONSOLE);
            return;
        }

        if(helpOPManager == null){
            Util.mWARN(commandSender, String.format(MessageLib.MODULE_OFFLINE, "HelpOP"));
            return;
        }

        AssiPlayer assiPlayer = ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(commandSender.getName());

        if(assiPlayer.hasPermission(PermissionLib.CMD.HELPOP_STAFF)){

            if(args.length < 2){
                Util.mINFO(assiPlayer, String.format(MessageLib.CORRECT_USAGE, "helpop <handle <id> | info <player | id>>", "Handle or get info about a HelpOP (only within session)"));
                return;
            }

            if(args[0].equalsIgnoreCase("handle")){
                int i;
                try {
                    i = Integer.parseInt(args[1]);
                }catch(NumberFormatException e){
                    Util.mINFO(commandSender, MessageLib.INVALID_NUMBER);
                    return;
                }

                HelpOP helpOP = helpOPManager.getHelpOP(i, false);

                if(helpOP != null){
                    helpOPManager.handle(helpOP.getID(), assiPlayer);
                }else{
                    Util.mINFO(assiPlayer, "HelpOP ID invalid.");
                    return;
                }

            }else if(args[0].equalsIgnoreCase("info")){

                int id;

                try{
                    id = Integer.parseInt(args[1]);
                }catch(NumberFormatException e){

                    String name = args[1];

                    Set<HelpOP> unhandledHelpOPs = helpOPManager.getUnhandledHelpOPof(name, 5);
                    Set<HelpOP> handledHelpOPs = helpOPManager.getHandledHelpOPof(name, 5);

                    if(!unhandledHelpOPs.isEmpty()) {
                        Util.mINFO(assiPlayer, "Unhandled HelpOPs of " + name + "\n");
                        unhandledHelpOPs.forEach(helpOP -> assiPlayer.sendMessage(new TextComponent("&7#" + helpOP.getID() + "&f: From: &7" + helpOP.getServer() + " &fat &7" + Util.formatDateDiff(helpOP.getSent())
                                + "&f: &c" + helpOP.getContent())));
                        assiPlayer.sendMessage(new TextComponent(Util.color("&7---------------------")));
                    }else{
                        Util.mINFO(assiPlayer, "No unhandled HelpOPs found for this session.");
                    }

                    if(!handledHelpOPs.isEmpty()) {
                        Util.mINFO(assiPlayer, "\nHandled HelpOPs of " + name + " (Within session, capped to 5) \n");
                        handledHelpOPs.forEach(helpOP -> assiPlayer.sendMessage(new TextComponent(Util.color("&7#" + helpOP.getID() + "&f: From: &7" + helpOP.getServer()
                                + " &fsent &7" + Util.formatDateDiff(helpOP.getSent())
                                + "ago &f: Handled by: &7" + helpOP.getHandler() + "&f: &c" + helpOP.getContent()))));
                        assiPlayer.sendMessage(new TextComponent(Util.color("&7---------------------")));
                    }else{
                        Util.mINFO(assiPlayer, "No handled HelpOPs found for this session.");
                    }

                    return;
                }

                HelpOP helpOP = helpOPManager.getHelpOP(id, true);

                if(helpOP != null){

                    Util.mINFO(assiPlayer, "HelpOP ID: "+helpOP.getID());
                    assiPlayer.sendMessage(new TextComponent(Util.color("From: &7"+helpOP.getSender())));
                    assiPlayer.sendMessage(new TextComponent(Util.color("Server: &7"+helpOP.getServer())));
                    assiPlayer.sendMessage(new TextComponent(Util.color("Sent: &7"+Util.formatDateDiff(helpOP.getSent())+" ago")));
                    assiPlayer.sendMessage(new TextComponent(Util.color("Handled: &7"+helpOP.isHandled())));
                    if(helpOP.isHandled()) assiPlayer.sendMessage(new TextComponent(Util.color("Handler: &7"+helpOP.getHandler())));
                    assiPlayer.sendMessage(new TextComponent(Util.color("Content: &7"+helpOP.getContent())));

                    return;
                }else{
                    Util.mINFO(assiPlayer, "HelpOP ID invalid.");
                    return;
                }

            }else{
                Util.mINFO(assiPlayer, MessageLib.INVALID_SUB_CMD);
                return;
            }

        }else{

            if(args.length < 1){
                Util.mINFO(assiPlayer, String.format(MessageLib.CORRECT_USAGE, "helpop <message>", "Get help from staff"));
                return;
            }

            String message = Util.getFinalArg(args, 0);
            helpOPManager.createHelpOP(assiPlayer, message);
            return;
        }







    }
}
