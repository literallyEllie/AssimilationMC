package net.assimilationmc.assibungee.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FriendData {

    private UUID uuid;
    // Friends - incoming and outgoing requests are handled in session
    private Map<UUID, String> friends;
    private List<UUID> incoming;
    // Settings
    private boolean allowRequests, seeFriendBroadcast, sendJoinLeave, seeJoinLeave;

    public FriendData(UUID uuid) {
        this.uuid = uuid;
        this.friends = Maps.newHashMap();
        this.incoming = Lists.newArrayList();
        this.allowRequests = true;
        this.seeFriendBroadcast = true;
        this.sendJoinLeave = true;
        this.seeJoinLeave = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<UUID, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<UUID, String> friends) {
        this.friends = friends;
    }

    public void addFriend(UUID uuid, String name) {
        friends.put(uuid, name);
    }

    public void removeFriend(UUID uuid) {
        friends.remove(uuid);
    }

    public List<UUID> getIncoming() {
        return incoming;
    }

    public void setIncoming(List<UUID> incoming) {
        this.incoming = incoming;
    }

    public void addIncoming(UUID uuid) {
        incoming.add(uuid);
    }

    public void removeIncoming(UUID uuid) {
        incoming.remove(uuid);
    }

    public boolean isAllowRequests() {
        return allowRequests;
    }

    public void setAllowRequests(boolean allowRequests) {
        this.allowRequests = allowRequests;
    }

    public boolean isSeeFriendBroadcast() {
        return seeFriendBroadcast;
    }

    public void setSeeFriendBroadcast(boolean seeFriendBroadcast) {
        this.seeFriendBroadcast = seeFriendBroadcast;
    }

    public boolean isSendJoinLeave() {
        return sendJoinLeave;
    }

    public void setSendJoinLeave(boolean sendJoinLeave) {
        this.sendJoinLeave = sendJoinLeave;
    }

    public boolean isSeeJoinLeave() {
        return seeJoinLeave;
    }

    public void setSeeJoinLeave(boolean seeJoinLeave) {
        this.seeJoinLeave = seeJoinLeave;
    }

    public String serializeSettings() {
        return allowRequests + ";#;" + seeFriendBroadcast + ";#;" + sendJoinLeave + ";#;" + sendJoinLeave;
    }

    // true;#;true;#;true;#;true

    public void deserializeSettings(String data) {
        if (data == null || data.isEmpty()) return;
        final String[] split = data.replace("\"", "").split(";#;");

        this.allowRequests = Boolean.valueOf(split[0]);
        this.seeFriendBroadcast = Boolean.valueOf(split[1]);
        this.sendJoinLeave = Boolean.valueOf(split[2]);
        this.seeJoinLeave = Boolean.valueOf(split[3]);
    }

}
