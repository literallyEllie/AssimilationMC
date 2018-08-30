package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;
import java.util.UUID;

public class CmdMessageToggle extends BungeeCommand {

    public CmdMessageToggle(AssiBungee assiBungee) {
        super(assiBungee, "messagetoggle", BungeeGroup.ADMIN, Lists.newArrayList("mtoggle", "msgtoggle", "msgoff", "msgon"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        Set<UUID> toggled = plugin.getMessageOff();

        boolean off = toggled.contains(player.getUniqueId());

        if (off) {
            toggled.remove(player.getUniqueId());
        } else toggled.add(player.getUniqueId());

        player.sendMessage(new ComponentBuilder("Toggled ").color(ChatColor.RED).append(off ? "on" : "off").color(C.V).
                append(" private messages.").color(ChatColor.RED).create());
    }

}
