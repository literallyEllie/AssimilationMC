package net.assimilationmc.gameapi.testgame;

import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;

public class TestGamePlugin extends GamePlugin {

    private AssiGame game;

    @Override
    public AssiGame setupGame() {
        game = new TestGame(this);


        return game;
    }

    @Override
    protected void end() {
        game.getGameModules().forEach(GameModule::end);
    }

}
