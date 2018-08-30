package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdMaps extends SubCommand {

    public CmdMaps(){
        super("maps", UHCPerm.CMD.ADMIN_MAPS, "uhc maps", "Lists the maps");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        sendPMessage(sender, "All maps:");
        getMapManager().getMaps().values().forEach(map -> sendMessage(sender, map.toString()));


    }
}
