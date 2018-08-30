package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSetLobby extends SubCommand {

    public CmdSetLobby(){
            super("setlobby", UHCPerm.CMD.ADMIN_SET_LOBBY, "uhc setlobby <map>", "Set the map lobby");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(isPlayer(sender)) {

            if (args.length == 2) {

                String map = args[1];

                boolean setup = getMapManager().getConstructionMap(map) != null;
                UHCMap uhcMap = getMapManager().getMap(map) == null ? getMapManager().getConstructionMap(map) : getMapManager().getConstructionMap(map);

                if (uhcMap == null) {
                    sendPMessage(sender, "Map doesn't exist.");
                    return;
                }

                SerializableLocation location = new SerializableLocation(((Player) sender).getLocation());
                getMapManager().setLobby(uhcMap.getName(), location, setup);
                sendPMessage(sender, "Set the lobby spawn for "+ UColorChart.VARIABLE+uhcMap.getName()+UColorChart.R+" to your position");


            }
        }
    }
}
