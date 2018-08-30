package net.assimilationmc.assicore.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class CommandManager extends Module {

    private List<AssiCommand> commands;
    private CommandMap minecraftCommands;

    public CommandManager(AssiPlugin plugin) {
        super(plugin, "Command Manager");
    }

    @Override
    protected void start() {
        commands = Lists.newArrayList();

        try {
            final Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            minecraftCommands = (CommandMap) f.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log(Level.SEVERE, "Failed to access raw Minecraft command map (Incompatible version? Needs 1.8.8!)");
        }

        new Reflections("net.assimilationmc.assicore.command.commands")
                .getSubTypesOf(AssiCommand.class).forEach(cmd -> {
            try {
                registerCommand((AssiCommand) cmd.getConstructors()[0].newInstance(getPlugin()));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException | ArrayIndexOutOfBoundsException e) {
                if (!e.getMessage().equals("wrong number of arguments")) {
                    log(Level.WARNING, "Failed to load command " + cmd.getSimpleName());
                    e.printStackTrace();
                }
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
    public void registerCommand(AssiCommand... command) {
        for (AssiCommand commands : command) {
            minecraftCommands.register("nO tAbBiNg hErE", commands);
            this.commands.add(commands);
        }
    }

    /**
     * Unregister command by instance
     *
     * @param command Instance to unregister
     */
    public void unregisterCommand(AssiCommand command) {
        commands.remove(command);
        minecraftCommands.getCommand(command.getLabel()).unregister(minecraftCommands);
    }

    /**
     * Get a command by class reference
     *
     * @param command Command class
     * @return The command instance, or null if not registered/doesn't exist.
     */
    public <T> AssiCommand getCommand(Class<T> command) {
        for (AssiCommand commands : this.commands) {
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
    public void unregisterCommand(Class<? extends AssiCommand> command) {
        unregisterCommand(getCommand(command));
    }

    /**
     * @return an unmodifiable list of all the commands registered with AssimilationMC.
     */
    public List<AssiCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

}
