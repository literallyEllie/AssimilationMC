package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdBGroup extends BungeeCommand {

    public CmdBGroup(AssiBungee assiBungee) {
        super (assiBungee, "bgroup", BungeeGroup.ADMIN, Lists.newArrayList(), "<player>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer target = UtilPlayer.get(args[0]);
        if (target == null) {
            couldNotFind(sender, "Player " + args[0]);
            return;
        }

        sender.sendMessage(new ComponentBuilder("User's primary group: ").color(C.C).append(UtilPlayer.groupOf(target).name()).color(C.V).create());
    }

}
