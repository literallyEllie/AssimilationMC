package net.assimilationmc.assibungee.discord.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class DCmdHelp extends DiscordCommand {

    private MessageEmbed embed;

    public DCmdHelp(DiscordManager discordManager) {
        super(discordManager, "help", "Help command", Lists.newArrayList(), "");

        EmbedBuilder embedBuilder = discordManager.getEmbedBuilder()
                .setTitle("Bot Help Menu");
        discordManager.getCommandMap().values().stream().filter(discordCommand -> discordCommand.getPermission()
                .equals(Permission.MESSAGE_WRITE)).forEach(discordCommand ->
                embedBuilder.addField(discordManager.getDiscordBotData().getCommandPrefix() +
                        discordCommand.getLabel(), discordCommand.getDescription(), true));
        this.embed = embedBuilder.build();
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {
        discordManager.messageChannel(commandEnvironment.getChannel(), embed);
    }

}
