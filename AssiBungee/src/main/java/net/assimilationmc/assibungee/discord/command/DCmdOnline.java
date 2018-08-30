package net.assimilationmc.assibungee.discord.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.md_5.bungee.api.config.ServerInfo;

public class DCmdOnline extends DiscordCommand {

    public DCmdOnline(DiscordManager discordManager) {
        super(discordManager, "online", "Shows how many players there are online on specified servers.", Permission.MESSAGE_WRITE,
                Lists.newArrayList(), "[server]");
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {
        Channel channel = commandEnvironment.getChannel();
        String[] args = commandEnvironment.getArgs();

        String targetServer = "all";

        if (args.length > 0) {
            targetServer = args[0];
        }

        int amount = 0;

        if (!targetServer.equals("all")) {
            ServerInfo serverInfo = discordManager.getPlugin().getProxy().getServerInfo(targetServer);
            if (serverInfo == null) {
                couldNotFind(commandEnvironment.getUser(), channel, "the server \"" + targetServer + "\"");
                return;
            }
            amount = serverInfo.getPlayers().size();
        } else {

            for (ServerInfo serverInfo : discordManager.getPlugin().getProxy().getServersCopy().values()) {
                amount += serverInfo.getPlayers().size();
            }
        }

        discordManager.messageChannel(channel, "There " + (amount < 2 ? "is" : "are") + " currently **" + amount + "** player" + (amount == 1 ? "" : "s") +
                (targetServer.equals("all") ? " on the network." : " online on the server `" + targetServer + "`."));

    }

}
