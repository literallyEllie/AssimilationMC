package net.assimilationmc.assibungee.discord.command.staff;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.discord.command.DiscordCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class DCmdClear extends DiscordCommand {

    public DCmdClear(DiscordManager discordManager) {
        super(discordManager, "clear", "Clear chat messages", Permission.MESSAGE_MANAGE, Lists.newArrayList(), "<amount>");
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {
        final TextChannel channel = commandEnvironment.getChannel();
        final Member member = commandEnvironment.getSender();
        final String[] args = commandEnvironment.getArgs();

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount < 1 || amount > 50) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            discordManager.tempMessage(channel, ":x: " + member.getAsMention() + ", please specify a number between 1 and 50.", 10, TimeUnit.SECONDS, null);
            return;
        }

        channel.getHistory().retrievePast(amount + 1).queue(messages -> {
            try {
                channel.deleteMessages(messages).queue(success -> discordManager.tempMessage(channel,
                        ":thumbsup: Cleared " + amount + " messages.", 10, TimeUnit.SECONDS, null));
            } catch (IllegalArgumentException e) {
                discordManager.tempMessage(channel, ":x: Failed to delete some messages as they are too old to touch", 7, TimeUnit.SECONDS, null);
            }
        });
    }

}
