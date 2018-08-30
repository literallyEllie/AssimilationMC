package net.assimilationmc.ellie.discord.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.Bukkit;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class DCmdOnline extends DisCommand {

    public DCmdOnline(){
        super("online", "Shows how many players are online", Permission.MESSAGE_WRITE, "online [query-player]");
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        e.getChannel().sendTyping().queue();
        if(args.length == 2){
            e.getChannel().sendMessage("Player "+args[1]+" is "+(Bukkit.getPlayer(args[1]) != null ? null : "not")+" online!").queue();
            return;
        }

        e.getChannel().sendMessage("There are currently "+ Bukkit.getOnlinePlayers().size()+" players online!").queue();

    }
}
