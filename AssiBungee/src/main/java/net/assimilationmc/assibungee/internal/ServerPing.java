package net.assimilationmc.assibungee.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.stream.Collectors;

public class ServerPing {

    private final int id;
    private final String serverId;
    private Map<String, Object> attributes;

    public ServerPing(int id, String serverId) {
        this.id = id;
        this.serverId = serverId;
        this.attributes = Maps.newHashMap();
    }

    public ServerPing(String[] serialized) {
        this.id = Integer.parseInt(serialized[0]);
        this.serverId = String.valueOf(serialized[1]);
        this.attributes = Maps.newHashMap();

        for (String s : serialized[2].split(" ")) {
            addAttribute(s.split(":")[0], s.split(":")[1]);
        }

    }

    public int getId() {
        return id;
    }

    public String getServerId() {
        return serverId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, Object object) {
        attributes.put(key, object);
    }

    public String getAttribute(String key) {
        return String.valueOf(attributes.get(key));
    }

    public String[] serialize() {
        return new String[]{String.valueOf(id), serverId, Joiner.on(" ").join(attributes.entrySet().stream().map(stringObjectEntry ->
                stringObjectEntry.getKey() + ":" + stringObjectEntry.getValue()).collect(Collectors.toList()))};
    }

}
