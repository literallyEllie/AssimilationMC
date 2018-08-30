package net.assimilationmc.assiuhc;

import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.game.singles.GameClassicSingles;
import net.assimilationmc.assiuhc.game.singles.GameDeathmatchSingles;
import net.assimilationmc.assiuhc.game.singles.GameSkyUHCSingles;
import net.assimilationmc.assiuhc.game.singles.GameTestSingles;
import net.assimilationmc.assiuhc.game.teamed.GameClassicTeamed;
import net.assimilationmc.assiuhc.game.teamed.GameDeathmatchTeamed;
import net.assimilationmc.assiuhc.game.teamed.scatter.GameTeamedScatter;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.module.GameModule;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AssiUHC extends GamePlugin {

    private UHCGame game;

    @Override
    public UHCGame setupGame() {
        if (!System.getProperties().containsKey("subType")
                || !System.getProperties().containsKey("map")) {

            File file = new File("STARTUP_PARAMS");
            if (!file.exists()) {
                throw new RuntimeException("Not enough information to start server.");
            }

            try {
                final BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    final String[] data = line.split("=");

                    String id = data[0];
                    String value = data[1].trim();

                    switch (id) {
                        case "subType":
                            if (System.getProperties().containsKey("subType")) continue;
                            System.setProperty("subType", value);
                            break;
                        case "map":
                            if (System.getProperties().containsKey("map")) continue;
                            System.setProperty("map", value);
                            break;
                        case "custom":
                            if (System.getProperties().containsKey("custom")) continue;
                            System.setProperty("custom", value);
                            break;
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException("Failed to read STARTUP_PARAMS", e);
            }

            Validate.notNull(System.getProperty("subType"), "Game subType cannot be null");
            Validate.notNull(System.getProperty("map"), "Game map cannot be null");

        }

        UHCGameSubType gameSubType = UHCGameSubType.valueOf(System.getProperty("subType"));

        D.d("Loading custom properties: " + System.getProperty("custom"));

        switch (gameSubType) {
//            case TEST_SINGLES:
//                return game = new GameTestSingles(this);
//            case TEST_TEAMED:
//                return game = new GameTestTeamed(this);
            case SINGLES_CLASSIC:
                return game = new GameClassicSingles(this);
            case SINGLES_DEATHMATCH:
                return game = new GameDeathmatchSingles(this);
            case SINGLES_SKY:
                return game = new GameSkyUHCSingles(this);
            case TEAMED_CLASSIC:
                return game = new GameClassicTeamed(this);
            case TEAMED_DEATHMATCH:
                return game = new GameDeathmatchTeamed(this);
            case TEAMED_SCATTER:
                return game = new GameTeamedScatter(this);
            default:
                return game = new GameClassicSingles(this);
        }


//        return null;
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void end() {
        game.getGameModules().stream().filter(GameModule::isActive).forEach(GameModule::end);
        game.getGameModules().clear();
    }
}
