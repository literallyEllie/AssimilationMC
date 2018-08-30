package net.assimilationmc.assibungee.discord;

import net.assimilationmc.assibungee.discord.command.DiscordCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Ellie in AssimilationMC
 * at 14/01/2018
 */
public class DiscordCommandEnvironment {

    private DiscordCommand executedCommands;
    private Member sender;
    private TextChannel channel;
    private Message message;
    private String[] args;

    public DiscordCommandEnvironment(DiscordCommand executedCommands, Member sender, TextChannel channel, Message message, String[] args) {
        this.executedCommands = executedCommands;
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.args = args;
    }

    public DiscordCommand getExecutedCommand() {
        return executedCommands;
    }

    public Member getSender() {
        return sender;
    }

    public User getUser() {
        return sender.getUser();
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    public String[] getArgs() {
        return args;
    }

}
