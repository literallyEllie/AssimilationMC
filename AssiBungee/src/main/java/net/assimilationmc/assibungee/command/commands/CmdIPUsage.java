package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;
import java.util.UUID;

public class CmdIPUsage extends BungeeCommand {

    public CmdIPUsage(AssiBungee assiBungee) {
        super(assiBungee, "ipusage", BungeeGroup.STAFF, Lists.newArrayList("ipuse"), "<ip>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String ip = args[0];

        final Set<UUID> uuids = plugin.getPlayerManager().getIpWatcher().get(ip);
        if (uuids == null) {
            sender.sendMessage(new ComponentBuilder("No accounts currently using that IP.").create());
            return;
        }

        sender.sendMessage(new ComponentBuilder(String.valueOf(uuids.size())).color(C.V).append(" accounts currently using that IP:").color(ChatColor.RED).create());
        for (UUID uuid : uuids) {
            ProxiedPlayer player = UtilPlayer.get(uuid);
            if (player == null) {
                sender.sendMessage(new ComponentBuilder(uuid.toString()).color(C.II).create());
                continue;
            }
            sender.sendMessage(new ComponentBuilder(player.getName()).color(C.II).append(" - ").color(C.C).append(player.getServer().getInfo().getName())
                    .color(C.V).create());
        }

    }

}
