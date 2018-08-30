package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assicore.command.AssiCommand;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.MapManager;
import net.assimilationmc.ellie.assiuhc.games.GameManager;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public abstract class SubCommand extends AssiCommand {

    public SubCommand(String name, String basePermission, String usage, String description, List<String> aliases){
        super(name, basePermission, usage, description, aliases);
    }

    public SubCommand(String name, String basePermission, String usage, String description){
        super(name, basePermission, usage, description);
    }

    public GameManager getGameManager(){
        return UHC.getPlugin(UHC.class).getGameManager();
    }

    public MapManager getMapManager(){
        return UHC.getPlugin(UHC.class).getMapManager();
    }

    @Override
    public void sendPMessage(CommandSender sender, String message) {
        super.sendMessage(sender, UHC.prefix+message);
    }

    @Override
    public String correctUsage() {
        return UColorChart.R + "/" + UColorChart.COMMAND_USAGE + getUsage() + UColorChart.R + " - " + UColorChart.COMMAND_DESC + getDescription() + UColorChart.R + ".";
    }
}
