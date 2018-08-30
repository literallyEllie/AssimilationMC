package net.assimilationmc.assicore.command;

import com.google.common.base.Joiner;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class AssiCommand extends Command implements CommandExecutor {

    protected final AssiPlugin plugin;
    private final Rank minRank;
    private String[] usage;
    private int requiredArgs = 0;
    private boolean requirePlayer = false;

    private CommandExecutor executor;

    /**
     * A cool command wrapper.
     * Register with {@link CommandManager#registerCommand(AssiCommand...)}
     *
     * @param plugin      Base plugin instance
     * @param label       Command label
     * @param description Command description
     * @param minRank     The required rank to execute
     * @param aliases     The aliases they could also write
     * @param usage       How to use the command, don't include the label.
     */
    public AssiCommand(AssiPlugin plugin, String label, String description, Rank minRank, List<String> aliases, String... usage) {
        super(label);
        this.plugin = plugin;
        if (!description.endsWith(".")) description = description + ".";
        this.description = description;
        this.minRank = minRank;
        setAliases(aliases);
        setUsage(usage);
        setPermissionMessage(C.II + "No permission.");
        this.executor = this;
    }

    /**
     * A cool command wrapper.
     * Rank to execute is Player (lowest)
     * Register with {@link CommandManager#registerCommand(AssiCommand...)}
     *
     * @param plugin      Base plugin instance
     * @param label       Command label
     * @param description Command description
     * @param aliases     The aliases they could also write
     * @param usage       How to use the command, don't include the label.
     */
    public AssiCommand(AssiPlugin plugin, String label, String description, List<String> aliases, String... usage) {
        this(plugin, label, description, Rank.PLAYER, aliases, usage);
    }

    /**
     * Get the required rank to execute the command
     *
     * @return The required rank to execute
     */
    public Rank getMinRank() {
        return minRank;
    }

    /**
     * @return minimum required arguments to execute the command.
     */
    public int getRequiredArgs() {
        return requiredArgs;
    }

    public abstract void onCommand(CommandSender sender, String usedLabel, String[] args);

    @Override
    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (executor != null) {
            return executor.onCommand(sender, this, commandLabel, args);
        }
        sender.sendMessage(prefix(commandLabel) + "Error executing command (executor null).");
        return true;
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            AssiPlayer player = asPlayer(sender);
            if (!player.getRank().isHigherThanOrEqualTo(minRank)
                    || (minRank == Rank.INFERNAL && (player.getRank().isLowStaff() || player.getRank() == Rank.BUILDER))) {
                sender.sendMessage(getPermissionMessage());
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender && requirePlayer) {
            sender.sendMessage("This command requires player state.");
            return false;
        }

        if (!checkArgs(sender, label, args)) return true;

        try {
            onCommand(sender, label, args);
        } catch (Throwable e) {
            sender.sendMessage(prefix(label) + C.II + "Error executing command. (" + e.getMessage() + ")");
            e.printStackTrace();
        }
        return true;
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
     * @param sender    The sender of the command
     * @param aliasUsed The alias they used
     * @param args      The arguments they provided
     * @return If it sent a usage message (i.e they did something wrong)
     */
    public boolean checkArgs(CommandSender sender, String aliasUsed, String[] args) {
        return this.checkArgs(sender, aliasUsed, args, this.requiredArgs);
    }

    /**
     * Check if the arguments are sufficient
     *
     * @param sender    The sender of the command
     * @param aliasUsed The alias they used
     * @param args      The arguments they provided
     * @param required  Required arguments they should provide.
     * @return If it sent a usage message (i.e they did something wrong)
     */
    public boolean checkArgs(CommandSender sender, String aliasUsed, String[] args, int required) {
        boolean check = args.length >= required;
        if (!check) {
            this.usage(sender, aliasUsed);
        }

        return check;
    }

    public void usage(CommandSender sender, String aliasUsed) {
        sender.sendMessage(prefix(aliasUsed) + "Usage: " + C.II + "/" + aliasUsed + " " + Joiner.on(" ").join(usage) + C.C + " - " + C.V + description);
    }

    /**
     * "Shorthand" async runnable doer.
     *
     * @param runnable To execute
     */
    protected void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
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
        final Player target = UtilPlayer.get(name);
        boolean online = target != null && target.isOnline();
        if (!online) {
            couldNotFind(p, name);
        }
        return online;
    }

    /**
     * Presumed cast check. Unsafe alone.
     *
     * @param sender The presumed player instance.
     * @return An AssiPlayer instance of the sender, from PlayerManager.
     */
    protected AssiPlayer asPlayer(CommandSender sender) {
        return plugin.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
    }

    /**
     * Tells them it couldn't find something
     *
     * @param p     Thing to send it to
     * @param thing What we couldn't find
     */
    public void couldNotFind(CommandSender p, String thing) {
        p.sendMessage(C.II + "Could not find \"" + C.V + thing + C.II + "\".");
    }

    /**
     * "Shorthand" is-player checker
     *
     * @param sender CommandSender to check
     * @return If the sender is a {@link Player}
     */
    public boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Deny console from using the command.
     * #rejected
     */
    protected void requirePlayer() {
        requirePlayer = true;
    }

    public String prefix(String alias) {
        return C.CN + (alias.length() == 2 ? alias.toUpperCase() : StringUtils.capitalize(alias)) + C.SS + C.C;
    }

}