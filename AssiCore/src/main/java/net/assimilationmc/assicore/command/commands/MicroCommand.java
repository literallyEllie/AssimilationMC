package net.assimilationmc.assicore.command.commands;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MicroCommand extends AssiCommand {

    private final String response;

    /**
     * A command type for very small commands that don't really need their own class.
     *
     * @param plugin   The plugin instance.
     * @param label    The command label.
     * @param response The response to the command when run.
     * @param aliases  The aliases for the command.
     */
    public MicroCommand(AssiPlugin plugin, String label, String response, List<String> aliases) {
        super(plugin, label, "A micro-command to show the " + label, aliases);
        this.response = response;
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        sender.sendMessage(prefix(usedLabel) + response);
    }

}
