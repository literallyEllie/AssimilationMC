package net.assimilationmc.assiuhc.logger;

import com.google.common.collect.Maps;

import java.util.Map;

public class GameLogEvent {

    private GameEventType gameLogEvent;
    private Map<String, String> params;

    public GameLogEvent(GameEventType gameLogEvent) {
        this.gameLogEvent = gameLogEvent;
        this.params = Maps.newHashMap();
    }

    public GameEventType getGameLogEvent() {
        return gameLogEvent;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public GameLogEvent addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

}
