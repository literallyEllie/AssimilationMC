package net.assimilationmc.ellie.assiuhc.command.admin;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.util.Util;
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
public class CmdSetBuilders extends SubCommand {

    public CmdSetBuilders(){
        super("setbuilders", UHCPerm.CMD.ADMIN_SET_BUILDERS, "uhc setbuilders <map> <builders..>", "Set builders of a map", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length > 2){

            boolean co = getMapManager().getConstructionMap(args[1]) != null;
            UHCMap map = getMapManager().getMap(args[1]) == null ? getMapManager().getConstructionMap(args[1]) : getMapManager().getMap(args[1]);

            if(map == null){
                sendPMessage(sender, "Map doesn't exist");
                return;
            }

            String allBuilders = Util.getFinalArg(args, 2);

            ArrayList<String> builderList = new ArrayList<>();

            if(args.length == 3){
                builderList.add(args[2].trim());

            }else {
                String[] builders = allBuilders.split(" ");

                for (String builder : builders) {
                    builderList.add(builder.trim());
                }
            }

            getMapManager().setBuilders(map.getName(), builderList, co);
            sendPMessage(sender, UColorChart.R+"Set builders to "+UColorChart.VARIABLE +Joiner.on(UColorChart.R+", "+UColorChart.VARIABLE).join(map.getBuilders()));
            return;
        }
        sendMessage(sender, correctUsage());

    }
}
