package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.games.TimeoutCountdown;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 24/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdRejoin extends SubCommand {

    public CmdRejoin(){
        super("rejoin", UHCPerm.CMD.REJOIN, "uhc rejoin", "Rejoin a game you disconnected");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(isPlayer(sender)) {

            if (getGameManager().getDisconnected().get(sender.getName()) != null){

                TimeoutCountdown countdown = getGameManager().getDisconnected().get(sender.getName());
             //  if(getGameManager().getGameByMapName(countdown.getGame()) != null){
               //     getGameManager().rejoinGame(((Player) sender), getGameManager().getGameByMapName(countdown.getGame()));
                //}else{
                    Util.mINFO(sender, "Your game has already ended.");
                //}

                return;
            }
            sendPMessage(sender, "You have no game to rejoin.");
        }
    }
}
