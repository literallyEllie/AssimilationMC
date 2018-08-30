package net.assimilationmc.assibungee.command;

import com.google.common.base.Joiner;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.assimilationmc.assibungee.util.UtilString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class BungeeCommand extends Command {

    protected final AssiBungee plugin;
    private BungeeGroup requiredGroup;
    private String[] usage;
    private int requiredArgs = 0;
    private boolean requirePlayer = false;

    /**
     * A cool command wrapper.
     * Register with {@link BungeeCommandManager#registerCommand(BungeeCommand...)}
     *
     * @param plugin  Base plugin instance
     * @param label   Command label
     * @param aliases The aliases they could also write
     * @param usage   How to use the command, don't include the label.
     */
    public BungeeCommand(AssiBungee plugin, String label, BungeeGroup group, List<String> aliases, String... usage) {
        super(label, null, aliases.toArray(new String[0]));
        this.plugin = plugin;
        this.requiredGroup = group;
        setUsage(usage);
    }

    /**
     * A cool command wrapper.
     * Rank to execute is Player (lowest)
     * Register with {@link BungeeCommandManager#registerCommand(BungeeCommand...)}
     *
     * @param plugin  Base plugin instance
     * @param label   Command label
     * @param aliases The aliases they could also write
     * @param usage   How to use the command, don't include the label.
     */
    public BungeeCommand(AssiBungee plugin, String label, List<String> aliases, String... usage) {
        this(plugin, label, BungeeGroup.PLAYER, aliases, usage);
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    @Override
    public final void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (!UtilPlayer.groupOf(sender).isHigherThanOrEqualTo(requiredGroup)) {
                sender.sendMessage(new ComponentBuilder("No permission.").color(C.II).create());
                return;
            }

        } else if (sender instanceof ConsoleCommandSender && requirePlayer) {
            sender.sendMessage(new ComponentBuilder("This command requires player state.").create());
            return;
        }

        if (!checkArgs(sender, args)) return;

        try {
            onCommand(sender, args);
        } catch (Throwable e) {
            sender.sendMessage(new ComponentBuilder(prefix()).append("Error executing command. (" + e.getMessage() + ")").color(C.II).create());
            e.printStackTrace();
        }
    }

    /**
     * Set the command executor + calculates the required arguments
     *
     * @param usage The command usage.
     */
    public void setUsage(String... usage) {
        this.usage = usage;
        this.requiredArgs = (int) Arrays.stream(usage).filter((s) -> s.contains("<")).count();
    }

    /**
     * Check if the arguments are sufficient
     *
     * @param sender The sender of the command
     * @param args   The arguments they provided
     * @return If it sent a usage message (i.e they did something wrong)
     */
    public boolean checkArgs(CommandSender sender, String[] args) {
        return this.checkArgs(sender, args, this.requiredArgs);
    }

    /**
     * Check if the arguments are sufficient
     *
     * @param sender   The sender of the command
     * @param args     The arguments they provided
     * @param required Required arguments they should provide.
     * @return If it sent a usage message (i.e they did something wrong)
     */
    public boolean checkArgs(CommandSender sender, String[] args, int required) {
        boolean check = args.length >= required;
        if (!check) {
            this.usage(sender);
        }

        return check;
    }

    public void usage(CommandSender sender) {
        sender.sendMessage(new ComponentBuilder(prefix()).append("Usage: ").
                append("/" + getName() + " " + Joiner.on(" ").join(usage)).color(C.II).create());
    }

    /**
     * "Shorthand" async runnable doer.
     *
     * @param runnable To execute
     */
    protected void runAsync(Runnable runnable) {
        plugin.getProxy().getScheduler().runAsync(this.plugin, runnable);
    }

    /**
     * Attempt to find a player, if it can't find the player,
     * It will tell them
     *
     * @param p    The thing to return to .
     * @param name The player to look for
     * @return If they exist.
     */
    public boolean getPlayer(CommandSender p, String name) {
        final ProxiedPlayer target = UtilPlayer.get(name);
        boolean online = target != null;
        if (!online) {
            couldNotFind(p, "Player " + name);
        }
        return online;
    }

    /**
     * Tells them it couldn't find something
     *
     * @param p     Thing to send it to
     * @param thing What we couldn't find
     */
    public void couldNotFind(CommandSender p, String thing) {
        p.sendMessage(new ComponentBuilder("Could not find \"").color(C.II).append(thing).color(C.V).append("\".").color(C.II).create());
    }

    /**
     * "Shorthand" is-player checker
     *
     * @param sender CommandSender to check
     * @return If the sender is a {@link ProxiedPlayer}
     */
    public boolean isPlayer(CommandSender sender) {
        return sender instanceof ProxiedPlayer;
    }

    /**
     * Deny console from using the command.
     * #rejected
     */
    protected void requirePlayer() {
        requirePlayer = true;
    }

    protected String prefix() {
        return C.CN + UtilString.capitalize(getName()) + C.SS + C.C;
    }

}