package net.assimilationmc.ellie.discord.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by Ellie on 16.7.17 for votifier.
 * Affiliated with www.minevelop.com
 */
public class DCmdClear extends DisCommand {

    public DCmdClear() {
        super("clear", "Clear x amount of messages", Permission.KICK_MEMBERS, "clear <amount>");
    }

    @Override
    protected void onCommand(GuildMessageReceivedEvent e, String[] args) {
        TextChannel textChannel = e.getChannel();

        if (args.length != 2) {
            textChannel.sendMessage(correctUsage()).queue();
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if(amount > 99) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            textChannel.sendMessage("Invalid amount.").queue();
            return;
        }

        e.getChannel().getHistory().retrievePast(amount + 1).queue(messages -> {
            try {
                e.getChannel().deleteMessages(messages).queue(success ->
                        e.getChannel().sendMessage("Cleared " + amount + " messages.").queue());
            } catch (IllegalArgumentException ex) {
                e.getChannel().sendMessage("Some of the messages are older than 2 weeks so they couldn't be deleted.").queue();
            }
        });

    }

}
