package net.assimilationmc.ellie.discord.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class DCmdIP extends DisCommand {

    public DCmdIP(){
        super("ip", "Sends server ip", Permission.MESSAGE_WRITE, "ip");
    }

    @Override
    protected void onCommand(GuildMessageReceivedEvent e, String[] args) {
        e.getChannel().sendMessage("The IP is currently not released!").queue();
    }
}
