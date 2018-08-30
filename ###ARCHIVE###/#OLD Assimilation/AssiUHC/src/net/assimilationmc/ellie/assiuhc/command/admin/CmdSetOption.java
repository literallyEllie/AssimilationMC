package net.assimilationmc.ellie.assiuhc.command.admin;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.game.SingledGameType;
import net.assimilationmc.ellie.assiuhc.game.TeamedGameType;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdSetOption extends SubCommand {

    private final List<String> options = Arrays.asList("mapsize", "type");

    public CmdSetOption(){
        super("setoption", UHCPerm.CMD.ADMIN_SET_OPTION, "uhc setoption <map> <mapsize | type> <value>", "Set an option for a map", Collections.singletonList("so"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 4){

            String map = args[1];
            String option = args[2];
            String value = args[3];

            boolean setup = getMapManager().getConstructionMap(map) != null;
            UHCMap uhcMap = getMapManager().getMap(map) == null ? getMapManager().getConstructionMap(map) : getMapManager().getConstructionMap(map);


            if(uhcMap == null){
                sendPMessage(sender, "Map doesn't exist ("+ UColorChart.VARIABLE+map+UColorChart.R+").");
                return;
            }

            if(!options.contains(option.toLowerCase())){
                sendPMessage(sender, "Option doesn't exist.");
                return;
            }

            if(option.equalsIgnoreCase("type")){

                try {
                    getMapManager().addSingledGameType(uhcMap.getName(), SingledGameType.valueOf(value.toUpperCase()), setup);
                }catch(IllegalArgumentException e){
                    try{
                        getMapManager().addTeamedGameType(uhcMap.getName(), TeamedGameType.valueOf(value.toUpperCase()), setup);
                    }catch(IllegalArgumentException ex){
                        sendPMessage(sender, "Invalid game type. Valid game types:");
                        sendMessage(sender, UColorChart.R+"Singles: "+UColorChart.VARIABLE+ Joiner.on(UColorChart.R+", "+UColorChart.VARIABLE).join(SingledGameType.values()));
                        sendMessage(sender, UColorChart.R+"Teamed: "+UColorChart.VARIABLE+ Joiner.on(UColorChart.R+", "+UColorChart.VARIABLE).join(TeamedGameType.values()));
                        return;
                    }
                }

                sendPMessage(sender, "Set "+UColorChart.VARIABLE+uhcMap.getName()+UColorChart.R+" to type value of "+UColorChart.VARIABLE+value+UColorChart.R+".");
                return;
            }

            int size;

            try{
                size = Integer.parseInt(args[3]);
            }catch(NumberFormatException e){
                sendPMessage(sender, MessageLib.INVALID_NUMBER);
                return;
            }


            if(size < 1){
                sendPMessage(sender, "Value must be greater than 0.");
                return;
            }

            switch (option.toLowerCase()){
                case "mapsize":
                    getMapManager().setMapSize(map, size, setup);
                    sendPMessage(sender, "Set the map size for map "+UColorChart.VARIABLE+map+UColorChart.R+" to "+UColorChart.VARIABLE+value+UColorChart.R+".");
                    break;
            }

        }else{
            sendMessage(sender, correctUsage());
        }

    }
}
