package net.assimilationmc.ellie.assicore.api;

import net.assimilationmc.ellie.assicore.util.JsonUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ellie on 19/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class FriendPlayer {

    private UUID uuid;
    private String name;
    private long lastSeen;
    private boolean opRequests;
    private boolean opJoin;
    private boolean opLeave;

    private List<UUID> friends;
    private List<UUID> requests;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOpRequests() {
        return opRequests;
    }

    public void setOpRequests(boolean opRequests) {
        this.opRequests = opRequests;
    }

    public boolean isOpJoin() {
        return opJoin;
    }

    public void setOpJoin(boolean opJoin) {
        this.opJoin = opJoin;
    }

    public boolean isOpLeave() {
        return opLeave;
    }

    public void setOpLeave(boolean opLeave) {
        this.opLeave = opLeave;
    }

    public List<UUID> getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        List<String> raw = JsonUtil.from_gson(friends);
        List<UUID> ny = new LinkedList<>();
        raw.forEach(s -> ny.add(UUID.fromString(s)));
        this.friends = ny;
    }

    public List<UUID> getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        List<String> raw = JsonUtil.from_gson(requests);
        List<UUID> ny = new LinkedList<>();
        raw.forEach(s -> ny.add(UUID.fromString(s)));
        this.requests = ny;
    }

}
