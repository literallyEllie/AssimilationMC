package net.assimilationmc.assibungee.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

public class BungeeCommandManager extends Module {

    private List<BungeeCommand> commands;

    public BungeeCommandManager(AssiBungee plugin) {
        super(plugin, "Command Manager");
    }

    @Override
    protected void start() {
        commands = Lists.newArrayList();

        new Reflections("net.assimilationmc.assibungee.command.commands")
                .getSubTypesOf(BungeeCommand.class).forEach(cmd -> {
            try {
                registerCommand((BungeeCommand) cmd.getConstructors()[0].newInstance(getPlugin()));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException e) {
                log(Level.WARNING, "Failed to load command " + cmd.getSimpleName());
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void end() {
        commands.clear();
    }

    /**
     * Register an array of AssiCommands
     *
     * @param command Command instance(s) to register
     */
    public void registerCommand(BungeeCommand... command) {
        for (BungeeCommand commands : command) {
            getPlugin().getProxy().getPluginManager().registerCommand(getPlugin(), commands);
            this.commands.add(commands);
        }
    }

    /**
     * Unregister command by instance
     *
     * @param command Instance to unregister
     */
    public void unregisterCommand(BungeeCommand command) {
        commands.remove(command);
        getPlugin().getProxy().getPluginManager().unregisterCommand(command);
    }

    /**
     * Get a command by class reference
     *
     * @param command Command class
     * @return The command instance, or null if not registered/doesn't exist.
     */
    public <T> BungeeCommand getCommand(Class<T> command) {
        for (BungeeCommand commands : this.commands) {
            if (commands.getClass().equals(command))
                return commands;
        }
        return null;
    }

    /**
     * Unregister a command by class reference.
     *
     * @param command Command class
     */
    public void unregisterCommand(Class<? extends BungeeCommand> command) {
        unregisterCommand(getCommand(command));
    }

}
