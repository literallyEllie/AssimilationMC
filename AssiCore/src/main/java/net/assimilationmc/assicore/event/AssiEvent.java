package net.assimilationmc.assicore.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssiEvent extends Event {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    /**
     * ezpz Event declaration
     *
     * @param async If the event is to be executed asynchronously.
     */
    public AssiEvent(boolean async) {
        super(async);
    }

    /**
     * Declare a synchronized event.
     */
    public AssiEvent() {
        this(false);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
