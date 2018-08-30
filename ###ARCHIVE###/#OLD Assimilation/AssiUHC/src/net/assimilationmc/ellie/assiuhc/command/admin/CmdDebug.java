package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.games.util.GameState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by Ellie on 17.7.17 for AssimilationMC.
 * Affiliated with www.minevelop.com
 */
public class CmdDebug extends SubCommand {

    public CmdDebug(){
        super("debug", Rank.ADMIN, "", "Debug command", Collections.singletonList(""));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player )sender;
        UHCGame game = getGameManager().getPlayerGame(player, true);
        if(game != null){
            game.getGameKeeperTask().setSeconds(100000);
            sendPMessage(sender, "set timer to 10,000 seconds");

            if(game.getGameState() == GameState.WARMUP || game.getGameState() == GameState.WAITING ){
                sendPMessage(sender, "spawning a dp");
                game.getDropManager().debugSpawn();
            }

        }

    }

}
