package net.assimilationmc.assicore.event.update;

import net.assimilationmc.assicore.event.AssiEvent;

public class UpdateEvent extends AssiEvent {

    private final UpdateType type;

    /**
     * To be called after a certain amount of time
     *
     * @param type The type of time pasted.
     */
    public UpdateEvent(UpdateType type) {
        this.type = type;
    }

    public UpdateType getType() {
        return type;
    }

}
