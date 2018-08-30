package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assiuhc.game.GameState;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdList extends SubCommand {

    public CmdList(){        //               0       1
        super("list", UHCPerm.CMD.LIST, "uhc list [waiting | ingame]", "Lists all initiated games (games with at least 1 player) with filters", Arrays.asList("l"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 2 && (args[1].equalsIgnoreCase("waiting") || args[1].equalsIgnoreCase("ingame"))){

            sendPMessage(sender, "Games in the state of "+ UColorChart.VARIABLE+args[1]+UColorChart.R+":");
            switch (args[1].toLowerCase()){
                case "waiting":
                    List<UHCGame> games = getGameManager().getGames().values().stream().filter(game -> game.getGameState() == GameState.WAITING).collect(Collectors.toList());
                   // games.forEach(game -> sendMessage(sender, "&6"+game.getMap().getName()+"&7: (&c"+game.getPlayers().size()+"&7/&6"+game.getMap().getMaxPlayers()+"&7)"));
                    break;
                case "ingame":
                    List<UHCGame> spill = getGameManager().getGames().values().stream().filter(game -> game.getGameState() == GameState.WARMUP || game.getGameState() == GameState.INGAME)
                            .collect(Collectors.toList());
                   // spill.forEach(game -> sendMessage(sender, "&6"+game.getMap().getName()+"&7: (&c"+game.getPlayers().size()+"&7/&6"+game.getMap().getMaxPlayers()+"&7)"));
                    break;
            }
        }
        else if(args.length == 1) {

            Collection<UHCGame> games = getGameManager().getGames().values();
            sendPMessage(sender, "All games");
            //games.forEach(game -> sendMessage(sender, "&6"+game.getMap().getName()+"&7: (&c"+game.getPlayers().size()+"&7/&6"+game.getMap().getMaxPlayers()+"&7) &6"+game.getGameState().name()));
        }
        else {
            sendMessage(sender, correctUsage());
        }

    }
}
