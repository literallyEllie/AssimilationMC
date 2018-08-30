package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 07/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSetMaterial extends SubCommand {

    public CmdSetMaterial(){
        super("setmaterial", UHCPerm.CMD.ADMIN_SET_MATERIAL, "uhc setmaterial <map>", "Set the display material for a map");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(!isPlayer(sender)) return;
        if (args.length == 2) {

            String map = args[1];

            boolean setup = getMapManager().getConstructionMap(map) != null;
            UHCMap uhcMap = getMapManager().getMap(map) == null ? getMapManager().getConstructionMap(map) : getMapManager().getConstructionMap(map);

            if (uhcMap == null) {
                sendPMessage(sender, "Map doesn't exist.");
                return;
            }

            Player player = (Player) sender;
            getMapManager().setDisplayMaterial(uhcMap.getName(), player.getItemInHand().getType(), setup);
            sendPMessage(sender, "Set the material for " + UColorChart.VARIABLE + uhcMap.getName() + UColorChart.R + " to "+ UColorChart.VARIABLE+player.getItemInHand().getType().name()+UColorChart.R+".");

        }


    }
}
