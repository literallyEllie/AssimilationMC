package net.assimilationmc.assibungee.command.commands.override;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.server.ServerName;
import net.assimilationmc.assibungee.util.C;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CmdGList extends BungeeCommand {

    public CmdGList(AssiBungee assiBungee) {
        super(assiBungee, "glist", BungeeGroup.ADMIN, Lists.newArrayList());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        final Collection<ServerInfo> values = plugin.getProxy().getServersCopy().values();

        List<String> lobbies = Lists.newArrayList();
        List<String> uhcs = Lists.newArrayList();

        for (ServerInfo value : values) {
            final List<String> players = value.getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList());
            if (value.getName().matches(ServerName.HUB)) {
                lobbies.addAll(players);
                continue;
            }
            uhcs.addAll(players);
        }

        sender.sendMessage(new ComponentBuilder("Total online: ").color(C.C).append(String.valueOf(lobbies.size() + uhcs.size())).color(C.V).create());

        sender.sendMessage(new ComponentBuilder("Lobbies: (").color(C.C).append(String.valueOf(lobbies.size())).color(C.V)
                .append(") ").color(C.C).append(C.V.toString()).append(Joiner.on(C.C + ", " + C.V).join(lobbies)).create());
        sender.sendMessage(new TextComponent());
        sender.sendMessage(new ComponentBuilder("UHCs: (").color(C.C).append(String.valueOf(uhcs.size())).color(C.V)
                .append(") ").color(C.C).append(C.V.toString()).append(Joiner.on(C.C + ", " + C.V).join(uhcs)).create());
    }

}
