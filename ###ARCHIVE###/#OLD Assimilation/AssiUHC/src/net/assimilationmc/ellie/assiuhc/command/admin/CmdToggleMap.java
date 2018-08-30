package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdToggleMap extends SubCommand {

    public CmdToggleMap(){
        super("togglemap", UHCPerm.CMD.ADMIN_TOGGLE, "uhc togglemap <map>", "Toggle a map for usage", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendMessage(sender, correctUsage());
            return;
        }

        boolean co = getMapManager().getConstructionMap(args[1]) != null;
        UHCMap map = getMapManager().getMap(args[1]) == null ? getMapManager().getConstructionMap(args[1]) : getMapManager().getMap(args[1]);

        if (map == null) {
            sendPMessage(sender, "Map doesn't exist.");
            return;
        }

        getMapManager().setToggled(map.getName(), !map.isToggled(), co);
        sendPMessage(sender, "Map is now " + UColorChart.VARIABLE + (map.isToggled() ? "enabled" : "disabled")+UColorChart.R+"."); // bc invert
    }

}
