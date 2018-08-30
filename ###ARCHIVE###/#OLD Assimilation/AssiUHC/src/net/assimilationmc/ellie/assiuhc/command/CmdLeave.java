package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdLeave extends SubCommand {

    public CmdLeave(){
        super("leave", UHCPerm.CMD.LEAVE, "uhc leave", "Leave a game");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (isPlayer(sender)) {

            Player p = (Player) sender;

            UHCGame game = getGameManager().getPlayerGame(p);

            if (game != null){
                getGameManager().quitGame(p, game, false);
            }else{
                sendPMessage(sender, "You're not in a game.");
            }
        }
    }
}
