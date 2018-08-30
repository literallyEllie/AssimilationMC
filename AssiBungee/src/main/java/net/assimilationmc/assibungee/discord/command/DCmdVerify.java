package net.assimilationmc.assibungee.discord.command;

import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.player.AssiPlayer;
import net.assimilationmc.assibungee.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.assimilationmc.assibungee.util.UtilString;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class DCmdVerify extends DiscordCommand {

    public DCmdVerify(DiscordManager discordManager) {
        super(discordManager, "verify", "Link your Discord account to your Minecraft account.", Permission.MESSAGE_WRITE,
                Collections.emptyList(), "<minecraft username>");
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {

        final TextChannel channel = commandEnvironment.getChannel();
        final String[] args = commandEnvironment.getArgs();
        final User sender = commandEnvironment.getUser();

        final ProxiedPlayer bukkitPlayer = UtilPlayer.get(args[0]);
        if (bukkitPlayer == null) {
            discordManager.tempMessage(channel, "Player `" + args[0] + "` was not found on the server.", 10, TimeUnit.SECONDS, null);
            return;
        }

        final AssiPlayer profile = discordManager.getPlugin().getPlayerManager().getPlayerRedis(bukkitPlayer.getUniqueId());

        if (profile == null) {
            discordManager.tempMessage(channel, "Your data was not found. Please log on to the network.", 10, TimeUnit.SECONDS, null);
            return;
        }

        if (profile.getDiscordAccount() != 0) {
            discordManager.tempMessage(channel, "This account is already verified.", 10, TimeUnit.SECONDS, null);
            return;
        }

        String code = UtilString.generateRandomString(7).toUpperCase();

        commandEnvironment.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your code is `"
                + code + "` Redeem it by doing `/verify <code>` in-game!").queue());
        discordManager.messageChannel(channel, sender.getAsMention() + ", You have been `Private Messaged` your verification code. Please check there for further instructions.");

        discordManager.getPlugin().getRedisManager().sendPubSubMessage("DISCORD", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                discordManager.getPlugin().getServerData().getId(), "VERIFY", new String[]{bukkitPlayer.getUniqueId().toString(),
                code, String.valueOf(sender.getIdLong())}));

    }

}
