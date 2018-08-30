package net.assimilationmc.assibungee.discord.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordLinkData;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class DCmdStats extends DiscordCommand {

    public DCmdStats(DiscordManager discordManager) {
        super(discordManager, "stats", "Get stats here in Discord", Lists.newArrayList());
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {

        DiscordLinkData discordLinkData = discordManager.getData(commandEnvironment.getUser().getIdLong());
        if (discordLinkData == null) {
            discordManager.messageChannel(commandEnvironment.getChannel(), "No data found for your account. If you recently added your account please relog.");
            return;
        }
        final MessageEmbed embed = discordManager.getEmbedBuilder()
                .setTitle("Stats about " + discordLinkData.getName() + ":")
                .setDescription("Verified :white_check_mark:")
                .addField("UUID", discordLinkData.getUuid().toString(), false)
                .addField("Rank", discordLinkData.getRank().getName(), false)
                .build();
        discordManager.messageChannel(commandEnvironment.getChannel(), embed);

    }

}
