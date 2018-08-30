package net.assimilationmc.gameapi.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.command.CommandSender;

public class CmdForcestart extends AssiCommand {

    private final AssiGame assiGame;

    public CmdForcestart(AssiGame assiGame) {
        super(assiGame.getPlugin(), "forcestart", "Command to force start the game (dev only)", Rank.DEVELOPER, Lists.newArrayList());
        this.assiGame = assiGame;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {

        //if (!plugin.getServerData().isDev()) {
          //  commandSender.sendMessage(GC.II + "This command can only be run on a development environment.");
          //  return;
        //}

        if (assiGame.getGamePhase() != GamePhase.LOBBY) {
            commandSender.sendMessage(GC.II + "This command is only valid in the Lobby game phase.");
            return;
        }

        if (assiGame.isForceStart()) {
            commandSender.sendMessage(GC.II + "Game is already being force-started.");
            return;
        }

        assiGame.setForceStart(true);
        assiGame.setCounter(assiGame.getAssiGameSettings().getLobbyTime() - 4);
        commandSender.sendMessage(GC.C + "Game counter fast-forwarded to " + GC.V + assiGame.getCounter() + GC.C + " seconds. (4 seconds before lobby time finishes)");
    }

}
