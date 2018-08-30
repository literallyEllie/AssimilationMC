package net.assimilationmc.assibungee.discord.command;

import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.dv8tion.jda.core.Permission;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DMicroCommand extends DiscordCommand {

    private final String response;

    /**
     * A command type for very small commands that don't really need their own class.
     *
     * @param discordManager The discord manager instance.
     * @param label          The command label.
     * @param response       The response to the command when run.
     * @param aliases        The aliases for the command.
     */
    public DMicroCommand(DiscordManager discordManager, String label, String response, List<String> aliases) {
        super(discordManager, label, "A micro-command to show the " + label, Permission.MESSAGE_WRITE, aliases);
        this.response = response;
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {
        discordManager.tempMessage(commandEnvironment.getChannel(),
                commandEnvironment.getSender().getAsMention() + ", " + response, 7, TimeUnit.SECONDS, commandEnvironment.getMessage());
    }

}
