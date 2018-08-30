package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.assimilationmc.assibungee.util.UtilString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdReply extends BungeeCommand {

    public CmdReply(AssiBungee assiBungee) {
        super(assiBungee, "reply", Lists.newArrayList("r"), "<message>");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        CmdMessage cmdMessage = (CmdMessage) plugin.getBungeeCommandManager().getCommand(CmdMessage.class);
        if (cmdMessage == null) {
            player.sendMessage(new ComponentBuilder("Error whilst accessing CmdMessage.class").color(C.II).create());
            return;
        }

        if (!cmdMessage.getMessageMap().containsKey(player.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("You have no one to reply to.").color(C.C).create());
            return;
        }

        ProxiedPlayer target = UtilPlayer.get(cmdMessage.getMessageMap().get(player.getUniqueId()));

        if (target == null) {
            couldNotFind(sender, "the person you last talked with");
            return;
        }

        final String message = UtilString.getFinalArg(args, 0);
        cmdMessage.onCommand(sender, new String[]{target.getName(), message});
    }

}