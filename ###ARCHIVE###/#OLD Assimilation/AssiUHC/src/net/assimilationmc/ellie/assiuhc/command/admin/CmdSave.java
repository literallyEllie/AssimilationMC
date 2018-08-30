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
public class CmdSave extends SubCommand {

    public CmdSave(){
        super("save", UHCPerm.CMD.ADMIN_SAVE, "uhc save <map>", "Save a map", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 2){

            UHCMap map = getMapManager().getConstructionMap(args[1]);

            if(map != null){
                if(getMapManager().finishConstructionMap(map.getName())){
                    sendPMessage(sender, "Map "+ UColorChart.VARIABLE+map.getName()+UColorChart.R+" has been successfully created.");
                }else{
                    sendPMessage(sender, "Map "+UColorChart.VARIABLE+map.getName()+UColorChart.R+" was not created.");
                }

            }else{
                if(getMapManager().getMap(args[1]) == null){
                    sendPMessage(sender, "Map doesn't exist.");
                }else{
                    getMapManager().saveMap(getMapManager().getMap(args[1]), false);
                    sendPMessage(sender, "Map "+UColorChart.VARIABLE+args[1]+UColorChart.R+" has been saved.");
                }
            }

        }else{
            sendMessage(sender, correctUsage());
        }

    }
}
