package net.assimilationmc.ellie.assiuhc.util;

import net.assimilationmc.ellie.assiuhc.game.SingledGameType;
import net.assimilationmc.ellie.assiuhc.game.TeamedGameType;

import java.util.UUID;

/**
 * Created by Ellie on 01/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameBuilder {

    private UUID builder;
    private final boolean teamed;
    private SingledGameType singledGameType;
    private TeamedGameType teamedGameType;
    private String map;

    public GameBuilder(boolean teamed){
        this.teamed = teamed;
    }

    public boolean isTeamed() {
        return teamed;
    }

    public SingledGameType getSingledGameType() {
        return singledGameType;
    }

    public void setSingledGameType(SingledGameType singledGameType) {
        this.singledGameType = singledGameType;
    }

    public TeamedGameType getTeamedGameType() {
        return teamedGameType;
    }

    public void setTeamedGameType(TeamedGameType teamedGameType) {
        this.teamedGameType = teamedGameType;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public UUID getBuilder() {
        return builder;
    }

    public void setBuilder(UUID builder) {
        this.builder = builder;
    }

}
