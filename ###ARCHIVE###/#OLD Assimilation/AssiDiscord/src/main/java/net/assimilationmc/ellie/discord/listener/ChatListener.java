package net.assimilationmc.ellie.discord.listener;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.util.Channels;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ChatListener extends ListenerAdapter {

    private final long self = 301722680028037130L;

    private final AssiDiscord discord;
    private final String prefix;

    public ChatListener(AssiDiscord discord, String prefix){
        this.discord = discord;
        this.prefix = prefix;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        JDA jda = event.getJDA();
        User user = event.getAuthor();
        Channel channel = event.getChannel();
        Message message = event.getMessage();
        long id = channel.getIdLong();

        if (user.getIdLong() == self) return;

        if (id == Channels.STAFF_CHAT) {
            AssiCore.getCore().getModuleManager().getStaffChatManager().messageBypass(user.getName(), message.getRawContent());
            return;
        }

        if ((message.getRawContent().startsWith(prefix))) {

            String msg = message.getRawContent().substring(prefix.length());

            String[] args = message.getRawContent().substring(prefix.length()).split(" ");

            discord.getCommands().forEach((s, discordCommand) -> {
                if (s.equalsIgnoreCase(args[0])) {
                    discordCommand.execute(event, args);
                } else {
                    discordCommand.getAliases().forEach(s1 -> {
                        if (s1.equalsIgnoreCase(args[0]))
                            discordCommand.execute(event, args);
                    });
                }
            });
        }
    }

}
