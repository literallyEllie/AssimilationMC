package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assicore.util.Callback;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.SpawnSetupTool;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Ellie on 10/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSpawns extends SubCommand {

    private HashMap<String, Callback<SpawnSetupTool>> spawnSetups = new HashMap<>();

    public CmdSpawns(){
        super("spawns", UHCPerm.CMD.ADMIN_SPAWNS, "uhc spawns <map>", "Spawn management");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(!isPlayer(sender)) return;

        if(args.length == 2){

            String map = args[1];
            if (getMapManager().getMap(map) == null) {
                sendPMessage(sender, "Map doesn't exist.");
                return;
            }

            if(spawnSetups.containsKey(map)){
                sendPMessage(sender, "A map with that name is already being modified.");
                return;
            }

            sendPMessage(sender, "Entering spawn edit mode.");
            setEditing(map, ((Player) sender));
        }else{
            sendMessage(sender, correctUsage());
        }

    }

    private void setEditing(String map, Player player) {
        new SpawnSetupTool(this, map, player);
        spawnSetups.put(map, (SpawnSetupTool data) -> {
            spawnSetups.remove(map);
            getMapManager().setTeamSpawns(map, data.getSpawns(), getMapManager().getConstructionMap(map) != null);
            sendPMessage(player, "Spawns setup.");
        });

    }

    public HashMap<String, Callback<SpawnSetupTool>> getSpawnSetups() {
        return spawnSetups;
    }

}
