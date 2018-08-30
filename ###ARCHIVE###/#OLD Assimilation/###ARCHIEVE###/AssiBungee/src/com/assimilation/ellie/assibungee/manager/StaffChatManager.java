package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.command.helpop.HelpOP;
import com.assimilation.ellie.assibungee.server.AssiPlayer;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class StaffChatManager implements IManager {

    private Set<String> toggledOff;

    @Override
    public boolean load() {
        toggledOff = new HashSet<>();
        return true;
    }

    @Override
    public boolean unload() {
        toggledOff.clear();
        return true;
    }

    @Override
    public String getModuleID() {
        return "staffchat";
    }

    public void message(CommandSender commandSender, String server, String message){
        if(message.trim().equals("toggle")){
            if(!toggledOff.contains(commandSender.getName())){
                Util.mINFO(commandSender, "Toggled off staff chat");
                toggledOff.add(commandSender.getName());
                return;
            }
            Util.mINFO(commandSender, "Toggled on staff chat");
            toggledOff.remove(commandSender.getName());
            return;
        }

        ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().stream().filter(assiPlayer -> assiPlayer.hasPermission(PermissionLib.STAFF_CHAT))
            .filter(assiPlayer -> !toggledOff.contains(assiPlayer.getName())).map(AssiPlayer::getBase).collect(Collectors.toSet()).forEach(proxiedPlayer ->
                        proxiedPlayer.sendMessage(new TextComponent(Util.color("&9&lStaff &7"+server+" &f| &c"+commandSender.getName()+"&f> &c"+message))));
    }

    public void helpopSentMessage(HelpOP helpOP){
        ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().stream().filter(assiPlayer -> assiPlayer.hasPermission(PermissionLib.STAFF_CHAT))
                .map(AssiPlayer::getBase).collect(Collectors.toSet()).forEach(proxiedPlayer ->
            proxiedPlayer.sendMessage(
                    new ComponentBuilder(Util.color("\n&9&lHelpOP #&8"+helpOP.getID())).append(helpOP.getServer()).color(ChatColor.GRAY)
                            .append(" | ").color(ChatColor.WHITE).append(helpOP.getSender()).color(ChatColor.RED).append(">").color(ChatColor.WHITE).append(helpOP.getContent())
                    .color(ChatColor.RED).append("\n").create()));
    }

    public void helpopHandleMessage(HelpOP helpOP){
        ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().stream().filter(assiPlayer -> assiPlayer.hasPermission(PermissionLib.STAFF_CHAT))
                .map(AssiPlayer::getBase).collect(Collectors.toSet()).forEach(proxiedPlayer ->
                proxiedPlayer.sendMessage(
                        new TextComponent(Util.color("&9&lHelpOP #&8"+helpOP.getID()+" &7handled by &c"+helpOP.getHandler()))));
    }

    public Set<String> getToggledOff() {
        return toggledOff;
    }
}
