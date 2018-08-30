package net.assimilationmc.gameapi.phase;

import net.assimilationmc.assicore.event.AssiEvent;

public class GamePhaseChangeEvent extends AssiEvent {

    private final GamePhase from, to;

    public GamePhaseChangeEvent(GamePhase from, GamePhase to) {
        this.from = from;
        this.to = to;
    }

    public GamePhase getFrom() {
        return from;
    }

    public GamePhase getTo() {
        return to;
    }

}
