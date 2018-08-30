package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assicore.api.AssiRegion;
import net.assimilationmc.ellie.assicore.util.Callback;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.RegionSelector;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdCreate extends SubCommand {

    public HashMap<String, Callback<RegionSelector>> editors = new HashMap<>();

    public CmdCreate(){
        super("create", UHCPerm.CMD.ADMIN_CREATE, "uhc create <map>", "Begins initiation process of creating a map", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(isPlayer(sender)) {
            if (args.length == 2) {

                String map = args[1];
                if (getMapManager().getMap(map) != null) {
                    sendPMessage(sender, "Map already exists.");
                    return;
                }

                if(getMapManager().getConstructionMap(map) != null){
                    sendPMessage(sender, "A map with this name is already in creation.");
                    return;
                }

                sendPMessage(sender, "Entering edit mode.");
                setEditing(map, ((Player) sender));

            }else{
                sendMessage(sender, correctUsage());
            }
        }

    }

    private void setEditing(String map, Player player){

        new RegionSelector(this, map, player.getName());
        editors.put(map, (RegionSelector data) -> {
                AssiRegion assiRegion = data.getRegion();
                editors.remove(map);
                getMapManager().createMap(map, assiRegion);
                sendPMessage(player, "Map created successfully. (No file created yet.)");
        });


    }

    public void setFinished(String map, RegionSelector regionSelector){
        if(editors.get(map) != null) {
            editors.get(map).run(regionSelector);
        }
    }

}
