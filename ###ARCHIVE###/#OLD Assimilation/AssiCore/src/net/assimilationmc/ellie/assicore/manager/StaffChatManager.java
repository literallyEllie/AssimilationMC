package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.util.Channels;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    public void message(CommandSender commandSender, String message){
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
                        proxiedPlayer.sendMessage(Util.color("&9&lStaff &f| &c"+commandSender.getName()+"&f> &c"+message)));
        AssiDiscord.getAssiDiscord().messageChannel(Channels.STAFF_CHAT, "*"+Util.getTime()+"* **"+commandSender.getName().replaceAll("_", "\\Q\\_\\E")+"**: `"+message+"`");
    }

    public void messageBypass(String sender, String message){
        ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().stream().filter(assiPlayer -> assiPlayer.hasPermission(PermissionLib.STAFF_CHAT))
                .filter(assiPlayer -> !toggledOff.contains(assiPlayer.getName())).map(AssiPlayer::getBase).collect(Collectors.toSet()).forEach(proxiedPlayer ->
                proxiedPlayer.spigot().sendMessage(TextComponent.fromLegacyText(Util.color("&9&lStaff &f| &c"+sender+"&f> &c"+message))));
    }

    public void helpopSentMessage(Player sender, String message){
        ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().stream().filter(assiPlayer -> assiPlayer.hasPermission(PermissionLib.STAFF_CHAT))
                .map(AssiPlayer::getBase).collect(Collectors.toSet()).forEach(proxiedPlayer ->
            proxiedPlayer.spigot().sendMessage(
                    new ComponentBuilder(Util.color("\n&9&lHelpOP ")).append(" | ").color(ChatColor.WHITE).
                            append(sender.getName()).color(ChatColor.RED).append("> ").color(ChatColor.WHITE).append(message)
                        .color(ChatColor.RED).append("\n").create()));
        AssiDiscord.getAssiDiscord().messageChannel(Channels.HELPOP, "*"+Util.getTime()+"* **HelpOP** "+sender.getName()+": `"+message+"`");
    }

    public void punishmentUpdateMessage(String punisher, String player, String type, String reason){
        ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().stream().filter(assiPlayer -> assiPlayer.hasPermission(PermissionLib.STAFF_CHAT))
                .map(AssiPlayer::getBase).collect(Collectors.toSet()).forEach(players ->
                players.sendMessage(Util.color("&a&lPUNISHMENTS &9"+punisher+" &c"+type+" &9"+player+" &cfor &9"+reason)));
    }

    public Set<String> getToggledOff() {
        return toggledOff;
    }

}
