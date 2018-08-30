package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assiuhc.ui.GameJoinMenu;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdJoin extends SubCommand {

    public CmdJoin(){
        super("join", UHCPerm.CMD.JOIN, "uhc join", "Join menu", Arrays.asList("j"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (isPlayer(sender)) {

            if (getGameManager().getPlayerGame(((Player) sender)) != null) {
                sendPMessage(sender, "You're already in a game!");
                return;
            }

            new GameJoinMenu(((Player) sender));

        /*   if (args.length == 2) {
                Integer integer;
                try{
                    integer = Integer.parseInt(args[1]);
                }catch(NumberFormatException e){
                    Util.mINFO_noP(sender, UHC.prefix+ MessageLib.INVALID_NUMBER);
                    return;
                }

                UHCGame game = getGameManager().getGameById(integer);

                if (game == null) {

                    UHCMap map = getMapManager().getMap(args[1]);

                    if (map == null) {
                        sendPMessage(sender, "Map doesn't exist");
                        return;
                    }

                    sendPMessage(sender, "Looks like you're first here, quickly getting the game ready...");
                    game = getGameManager().startGame(map);
                }

                if (game.getGameState() == GameState.FINISHED) {
                    sendPMessage(sender, "&cThis game has already finished");
                    return;
                }

                sendPMessage(sender, "Sending you to game " + game.getMap().getName());
                getGameManager().joinGame(((Player) sender), game);
            } else {
                sendPMessage(sender, correctUsage());
            }
                */
        }

    }
}
