package net.assimilationmc.assibungee.discord.command.staff;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.discord.command.DiscordCommand;
import net.assimilationmc.assibungee.util.UtilString;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class DCmdBan extends DiscordCommand {

    public DCmdBan(DiscordManager discordManager) {
        super(discordManager, "ban", "Ban a user", Permission.BAN_MEMBERS, Lists.newArrayList(), "<who>", "[reason]");
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {
        final String[] args = commandEnvironment.getArgs();

        final User toBan = discordManager.parseUser(args[0]);
        if (toBan == null) {
            couldNotFind(commandEnvironment.getUser(), commandEnvironment.getChannel(), "User " + args[0]);
            return;
        }

        String reason = null;
        if (args.length > 1) {
            reason = UtilString.getFinalArg(args, 1);
        }

        discordManager.modLog(discordManager.getEmbedBuilder(DiscordManager.DiscordColor.BAN)
                .setTitle("Banned " + toBan.getName() + "#" + toBan.getDiscriminator() + " (" + toBan.getId() + ")")
                .addField("Banner", (commandEnvironment.getUser().getName() + "#" + commandEnvironment.getUser().getDiscriminator()), true)
                .addField("Guild", commandEnvironment.getChannel().getGuild().getName(), true)
                .addField("Reason", (reason != null ? reason : "No reason specified."), false));

        discordManager.tempMessage(commandEnvironment.getChannel(), ":ok_hand: Banned " + toBan.getName() + "#" + toBan.getDiscriminator()
                + (reason != null ? " (`" + reason + "`)" : ""), 10, TimeUnit.SECONDS, commandEnvironment.getMessage());

        commandEnvironment.getMessage().getGuild().getController().ban(toBan, 7, "[" + commandEnvironment.getUser().getName() + "] "
                + (reason != null ? reason : "No reason provided.")).queue();

        // TODO Log
    }

}
