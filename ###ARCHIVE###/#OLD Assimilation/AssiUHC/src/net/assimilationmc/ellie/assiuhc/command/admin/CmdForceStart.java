package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 24/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdForceStart extends SubCommand {

    public CmdForceStart(){
        super("forcestart", UHCPerm.CMD.FORCE_START, "uhc forcestart [id]", "Force starts a game or the game you're in");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        UHCGame game;

        if (args.length == 2) {
            Integer integer;
            try{
                integer = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                sendPMessage(sender, MessageLib.INVALID_NUMBER);
                return;
            }
            game = getGameManager().getGameById(integer);
        } else if (args.length == 1 && isPlayer(sender)) {
            game = getGameManager().getPlayerGame(((Player) sender));
        } else {
            sendMessage(sender, correctUsage());
            return;
        }

        if (game != null) {
            if (!game.getGameKeeperTask().forceStart()) {
                sendPMessage(sender, "This game cannot be force-started.");
                return;
            }
            sendPMessage(sender, "Game timer set to 6 seconds, if the game pre-conditions are not met, the game will not commence.");
            return;
        }
        sendPMessage(sender, "Game not found.");
    }
}
