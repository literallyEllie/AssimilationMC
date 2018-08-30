package net.assimilationmc.assiuhc.logger;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.UtilTime;

import java.util.Map;

public class UHCGameLogger {

    private Map<Long, GameLogEvent> gameLogEventMap;

    public UHCGameLogger() {
        this.gameLogEventMap = Maps.newLinkedHashMap();
    }

    public Map<Long, GameLogEvent> getGameLogEventMap() {
        return gameLogEventMap;
    }

    public GameLogEvent newEvent(GameEventType gameEventType) {
        GameLogEvent gameLogEvent = new GameLogEvent(gameEventType);
        gameLogEventMap.put(UtilTime.now(), gameLogEvent);
        return gameLogEvent;
    }

}
