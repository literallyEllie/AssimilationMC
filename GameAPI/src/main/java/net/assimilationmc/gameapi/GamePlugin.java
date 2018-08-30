package net.assimilationmc.gameapi;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.gameapi.cmd.CmdForcestart;
import net.assimilationmc.gameapi.cmd.CmdSpecChatToggle;
import net.assimilationmc.gameapi.game.AssiGame;

public abstract class GamePlugin extends AssiPlugin {

    private AssiGame assiGame;

    @Override
    protected void start() {
        this.assiGame = setupGame();

        getCommandManager().registerCommand(new CmdForcestart(assiGame), new CmdSpecChatToggle(assiGame));
    }

    public abstract AssiGame setupGame();

    public AssiGame getAssiGame() {
        return assiGame;
    }

}