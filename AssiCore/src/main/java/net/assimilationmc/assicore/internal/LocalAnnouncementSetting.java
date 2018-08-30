package net.assimilationmc.assicore.internal;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.Bukkit;

public class LocalAnnouncementSetting implements RedisChannelSubscriber {

    private boolean acceptingAnnouncements;

    /**
     * A class which listens for incoming announcements from the proxy and evaulates whether to
     * broadcast them or not to the local server.
     * <p>
     * The only reason this is toggleable as for certain games, we perhaps don't want players to be
     * spammed with announcements, or more less-generic announcements to be sent.
     *
     * @param assiPlugin the plugin instance.
     */
    public LocalAnnouncementSetting(AssiPlugin assiPlugin) {
        this.acceptingAnnouncements = true;

        assiPlugin.getRedisManager().registerChannelSubscriber("ANNOUNCEMENTS", this);
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        if (acceptingAnnouncements && UtilServer.getOnlinePlayers() > 0 && message.getSubject().equals("HELLO")) {
            Bukkit.getOnlinePlayers().forEach(o -> o.sendMessage(C.SS + message.getArgs()[0]));
        }
    }

    /**
     * @return is the server going to broadcast announcements? true: yes, false: no.
     */
    public boolean isAcceptingAnnouncements() {
        return acceptingAnnouncements;
    }

    /**
     * Set whether if the server should accept and broadcast announcements (dispatched from the proxy)
     *
     * @param acceptingAnnouncements true, yes we should. false, no lets ignore them >:)
     */
    public void setAcceptingAnnouncements(boolean acceptingAnnouncements) {
        this.acceptingAnnouncements = acceptingAnnouncements;
    }

}
