package net.assimilationmc.assicore.internal;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.commands.CmdVerify;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.UtilPlayer;

import java.util.UUID;

public class DiscordCommunicator implements RedisChannelSubscriber {

    private static final String CHANNEL = "DISCORD";
    private final AssiPlugin plugin;

    /**
     * A class to communicate to Discord with, to send messages and stuff, not on the internal channels.
     *
     * @param plugin plugin instance.
     */
    public DiscordCommunicator(AssiPlugin plugin) {
        this.plugin = plugin;
        plugin.getRedisManager().registerChannelSubscriber(CHANNEL, this);
    }

    /**
     * Send a Discord message to a preset channel.
     *
     * @param channelId The preset channel to message.
     * @param message   the message to send.
     */
    public void messageChannel(String channelId, String message) {
        message("MESSAGE", new String[]{channelId, message});
    }

    /**
     * Send a message to discord.
     *
     * @param subject The message subject/action.
     *                "MESSAGE" to send a message.
     * @param args    the arguments,
     *                args[0] a discord preset channel
     *                args[1] the message.
     */
    private void message(String subject, String[] args) {
        plugin.getRedisManager().sendPubSubMessage(CHANNEL, new RedisPubSubMessage(PubSubRecipient.PROXY,
                plugin.getServerData().getId(), subject, args));
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        String[] args = message.getArgs();

        if (message.getSubject().equals("VERIFY")) {
            UUID uuid = UUID.fromString(args[0]);
            if (UtilPlayer.get(uuid) == null)
                return;


            final CmdVerify command = (CmdVerify) plugin.getCommandManager().getCommand(CmdVerify.class);
            if (command == null) return;

            command.getCodes().put(uuid, args[1]);
            command.getIdIndex().put(uuid, Long.valueOf(args[2]));
        }

    }


}
