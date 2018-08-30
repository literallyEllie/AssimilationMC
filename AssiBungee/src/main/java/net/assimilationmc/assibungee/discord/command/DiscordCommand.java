package net.assimilationmc.assibungee.discord.command;

import com.google.common.base.Joiner;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.discord.DiscordPresetChannel;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.md_5.bungee.api.ProxyServer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DiscordCommand {

    protected final DiscordManager discordManager;

    private final String label;
    private final String description;
    private final Permission permission;

    private final List<String> aliases;
    private final String[] usage;

    private final int requiredArgs;

    /**
     * Discord command which can be only be executed from the discord bot.
     *
     * @param discordManager Discord Manager instance
     * @param label          Command label
     * @param description    Command description
     * @param permission     Command permission (Set to {@link Permission#MESSAGE_WRITE} if none)
     * @param aliases        Command aliases
     * @param usage          Command usage, don't include label.
     */
    public DiscordCommand(DiscordManager discordManager, String label, String description, Permission permission, List<String> aliases, String... usage) {
        this.discordManager = discordManager;
        this.label = label;
        this.description = description;
        this.permission = permission;
        this.aliases = aliases;
        this.usage = usage;
        this.requiredArgs = (int) Arrays.stream(usage).filter(s -> s.contains("<")).count();
    }

    /**
     * Discord command which can be only be executed from the discord bot.
     * This constructor using the MESSAGE_WRITE permission, so anyone can execute it.
     *
     * @param discordManager Discord Manager instance
     * @param label          Command label
     * @param description    Command description
     * @param aliases        Command aliases
     * @param usage          Command usage, don't include label.
     */
    public DiscordCommand(DiscordManager discordManager, String label, String description, List<String> aliases, String... usage) {
        this(discordManager, label, description, Permission.MESSAGE_WRITE, aliases, usage);
    }

    protected abstract void onCommand(DiscordCommandEnvironment commandEnvironment);

    /**
     * Command executor before {@link #onCommand(DiscordCommandEnvironment)}  is called.
     *
     * @param e    Event instance.
     * @param args Command arguments.
     */
    public final void onCommand(GuildMessageReceivedEvent e, String[] args) {
        final User user = e.getAuthor();
        final TextChannel channel = e.getChannel();
        try {
            if (PermissionUtil.checkPermission(e.getChannel(), e.getMember(), permission)) {
                final String[] arguments = new String[args.length - 1];
                System.arraycopy(args, 1, arguments, 0, args.length - 1);

                if (!checkArgs(user, channel, args[0], arguments)) return;

                final DiscordCommandEnvironment discordCommandEnvironment = new DiscordCommandEnvironment(this,
                        e.getMember(), channel, e.getMessage(), arguments);
                ProxyServer.getInstance().getScheduler()
                        .runAsync(discordManager.getPlugin(), () -> onCommand(discordCommandEnvironment));
            } else {
                discordManager.tempMessage(channel, ":thumbsdown: No permission " + user.getAsMention() + ".", 10,
                        TimeUnit.SECONDS, e.getMessage());
            }
        } catch (Throwable ex) {
            discordManager.messageChannel(DiscordPresetChannel.BOT_LOGS,
                    "Error executing command " + label +
                            " (" + ex.getMessage() + ") Args: " + Joiner.on(", ").join(args));
            discordManager.messageChannel(channel, ":thumbsdown: An error occurred whilst executing command " + label);
            ex.printStackTrace();
        }

    }

    /**
     * Get command label
     *
     * @return Command label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get command description
     *
     * @return Command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get command permission
     *
     * @return Command permission
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * Get command aliases
     *
     * @return Command aliases
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Get command usage
     *
     * @return Usage array
     */
    public String[] getUsage() {
        return usage;
    }

    /**
     * Get required arg count
     *
     * @return Required argument count.
     */
    public int getRequiredArgs() {
        return requiredArgs;
    }

    /**
     * Check if the arguments are sufficient
     *
     * @param sender    The sender
     * @param channel   The channel to return into.
     * @param aliasUsed The alias they used
     * @param args      The argements they provided
     * @return If it sent a usage message (i.e they did something wrong)
     */
    public boolean checkArgs(User sender, Channel channel, String aliasUsed, String[] args) {
        return this.checkArgs(sender, channel, aliasUsed, args, this.requiredArgs);
    }

    /**
     * Check if the arguments are sufficient
     *
     * @param sender    The sender
     * @param channel   The channel to return into.
     * @param aliasUsed The alias they used
     * @param args      The arguments they provided
     * @param required  Argument count required
     * @return If it sent a usage message (i.e they did something wrong)
     */
    public boolean checkArgs(User sender, Channel channel, String aliasUsed, String[] args, int required) {
        boolean check = args.length >= required;
        if (!check) {
            this.usage(sender, channel, aliasUsed);
        }
        return check;
    }

    /**
     * Send a return message correcting them into said channel
     *
     * @param sender    Person who did it wrong
     * @param channel   Channel to send it to
     * @param aliasUsed The alias they used.
     */
    public void usage(User sender, Channel channel, String aliasUsed) {
        discordManager.tempMessage(channel, "Incorrect usage " + sender.getAsMention() + "! Use " +
                discordManager.getDiscordBotData().getCommandPrefix() + aliasUsed + " " + Joiner.on(" ").join(usage) +
                " **-** " + description, 15, TimeUnit.SECONDS, null);
    }

    /**
     * A quick message to express you couldn't find something.
     *
     * @param sender  The person to notify
     * @param channel The channel to send it to
     * @param thing   The thing we couldn't find it.
     */
    public void couldNotFind(User sender, Channel channel, String thing) {
        discordManager.tempMessage(channel, sender.getAsMention() + " Could not find `" + thing.replace("`", "'") + "`.", 10, TimeUnit.SECONDS, null);
    }

}
