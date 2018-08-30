package net.assimilationmc.ellie.assicore.event.update;

import net.assimilationmc.ellie.assicore.event.AssiEvent;

public class UpdateEvent extends AssiEvent {

    private final UpdateType type;

    public UpdateEvent(UpdateType type) {
        this.type = type;
    }

    public UpdateType getUpdateType() {
        return this.type;
    }

}
