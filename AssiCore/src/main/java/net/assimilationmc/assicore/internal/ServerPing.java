package net.assimilationmc.assicore.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilJson;

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

        String attr = serialized[2];

        if (attr.contains("custom:")) {
            int start = attr.indexOf('{');
            int end = attr.indexOf('}') + 1;
            String customArgs = attr.substring(start, end);
            attributes.put("custom", UtilJson.deserialize(new Gson(), customArgs));

            attr = attr.replace("custom:" + customArgs, "").trim();
        }

        for (String s : attr.split(" ")) {
            if (s.isEmpty()) continue;
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
                stringObjectEntry.getKey().trim() + ":" + stringObjectEntry.getValue()).collect(Collectors.toList()))};
    }

}
