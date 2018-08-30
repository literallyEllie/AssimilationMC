package net.assimilationmc.assicore.friend;

import net.assimilationmc.assicore.event.AssiEvent;
import net.assimilationmc.assicore.player.AssiPlayer;

public class FriendMakeEvent extends AssiEvent {

    private AssiPlayer sender;
    private AssiPlayer acceptor;

    public FriendMakeEvent(AssiPlayer sender, AssiPlayer acceptor) {
        this.sender = sender;
        this.acceptor = acceptor;
    }

    public AssiPlayer getSender() {
        return sender;
    }

    public AssiPlayer getAcceptor() {
        return acceptor;
    }

}
