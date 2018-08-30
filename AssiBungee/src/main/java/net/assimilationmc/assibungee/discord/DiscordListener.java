package net.assimilationmc.assibungee.discord;

import net.assimilationmc.assibungee.command.commands.override.CmdAlert;
import net.assimilationmc.assibungee.discord.command.DiscordCommand;
import net.assimilationmc.assibungee.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.D;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class DiscordListener extends ListenerAdapter {

    private DiscordManager discordManager;

    public DiscordListener(DiscordManager discordManager) {
        this.discordManager = discordManager;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final User user = event.getAuthor();
        if (user.getIdLong() == DiscordManager.PRESUMED_SELF || user.isBot()) return;

        final String message = event.getMessage().getContentRaw();
        final String prefix = discordManager.getDiscordBotData().getCommandPrefix();

        // Command execution check
        if (message.startsWith(prefix)) {
            final String[] args = message.substring(prefix.length()).split(" ");

            final DiscordCommand discordCommand = discordManager.getCommand(args[0].trim(), true);
            if (discordCommand != null) {
                discordCommand.onCommand(event, args);
                return;
            }
        }

        /*
        if (message.toLowerCase().contains("yeet")) {
            int i = new Random().nextInt(3);

            switch (i) {
                case 0:
                    discordManager.messageChannel(event.getChannel(), "Yeet!");
                    break;
                case 1:
                    discordManager.messageChannel(event.getChannel(), "Ya Yeet!");
                    break;
                case 2:
                    discordManager.messageChannel(event.getChannel(), "YEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEET");
                    break;
            }

        }
        */

        if (!passSpam(event.getMessage(), message)) return;
        // more to come

        if (event.getChannel().getIdLong() == DiscordPresetChannel.STAFF_CHAT.getId()) {

            DiscordLinkData discordLinkData = discordManager.getData(user.getIdLong());
            if (discordLinkData == null) {
                discordManager.tempMessage(event.getChannel(), user.getAsMention() + ", " +
                        "You must verify your account before you can speak here.", 7, TimeUnit.SECONDS, event.getMessage());
                return;
            }

            discordManager.getPlugin().getRedisManager().sendPubSubMessage("staffchat", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                    discordManager.getPlugin().getServerData().getId(), "MESSAGE_SEND",
                    new String[]{discordLinkData.getName(), "potato", "Discord", message}));
            return;
        }

        if (event.getChannel().getIdLong() == DiscordPresetChannel.BROADCAST.getId()) {
            ProxyServer.getInstance().broadcast(new TextComponent(CmdAlert.FORMAT.replace("{message}", message)));
            discordManager.messageChannel(event.getChannel(), "[D] `" + user.getName() + "` sent a broadcast of `" + message + "` to the whole network.");
            return;
        }

    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        if (e.getGuild().getIdLong() != BotGuilds.PUBLIC.getId()) return;
        final User user = e.getMember().getUser();
        discordManager.messageChannel(DiscordPresetChannel.PUBLIC_GENERAL, ":wave: See ya around " + user.getName() + "#" + user.getDiscriminator() + "!");
    }

    /**
     * Checks if a message would pass a spam check
     *
     * @param message Message to check
     * @param input   Message content because effort
     * @return if it passed or not (a message is sent if true)
     */
    public boolean passSpam(Message message, String input) {
        // Tag spam check
        Matcher matcher = discordManager.getPatternUser().matcher(input);
        int hits = 0;
        while (matcher.find())
            hits++;

        final EmbedBuilder msgDelete = discordManager.getEmbedBuilder(DiscordManager.DiscordColor.MESSAGE_DELETE)
                .setTitle("Message deletion from " + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " (" + message.getAuthor().getIdLong() + ")" +
                        " in #" + message.getChannel().getName());

        // yay for java
        final int finalHits = hits;
        if (finalHits > 7) {
            message.delete().queue(poop -> discordManager.modLog(msgDelete
                    .addField("Message Content:", message.getContentRaw(), false)
                    .addField("Reason:", "Tag spamming. (" + finalHits + ")", false)));
            return false;
        }

        // etc ...

        return true;
    }

}
