package net.assimilationmc.ellie.assiuhc.command.admin;

import com.google.common.base.Joiner;
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
public class CmdInfo extends SubCommand {

    public CmdInfo(){
        super("info", UHCPerm.CMD.ADMIN_INFO, "uhc info <map>", "Gives information about a certain map", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 2){

            boolean co = getMapManager().getConstructionMap(args[1]) != null;
            UHCMap map = getMapManager().getMap(args[1]) == null ? getMapManager().getConstructionMap(args[1]) : getMapManager().getMap(args[1]);

            if(map == null){
                sendPMessage(sender, "Map doesn't exist.");
                return;
            }

            String r = UColorChart.R;
            String v = UColorChart.VARIABLE;

            sendPMessage(sender, "Information about map "+map.getName()+":");
            sendMessage(sender, "\n"+r+"In construction: "+v+co);
            sendMessage(sender, r+"Has lobby: "+v+(map.getLobbySpawn() != null));
            sendMessage(sender, r+"For teams: "+v+map.isForTeams());
            sendMessage(sender, r+"Game type: "+v+(map.isForTeams() ? map.getTeamedGameTypes().get(map.getSelectedTeamed()).getFriendly() :
                    map.getSingledGameType().get(map.getSelectedSingled()).getFriendly()));
            sendMessage(sender, r+"Spawns: "+v+map.getTeamSpawns().size());
            sendMessage(sender, r+"Builders: "+v+ Joiner.on(r+", "+v).join(map.getBuilders()));
        }else{
            sendPMessage(sender, correctUsage());
        }

    }
}
