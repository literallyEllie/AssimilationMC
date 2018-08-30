package net.assimilationmc.assicore.party;

import net.assimilationmc.assicore.event.AssiEvent;
import net.assimilationmc.assicore.player.AssiPlayer;

public class PartyJoinEvent extends AssiEvent {

    private final Party party;
    private final AssiPlayer joiner;

    public PartyJoinEvent(Party party, AssiPlayer joiner) {
        this.party = party;
        this.joiner = joiner;
    }

    public Party getParty() {
        return party;
    }

    public AssiPlayer getJoiner() {
        return joiner;
    }

}
