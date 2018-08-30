package net.assimilationmc.assicore.event;

import com.google.common.collect.Maps;

import java.util.Map;

public class GamePingEvent extends AssiEvent {

    private final Map<String, Object> attributes;

    public GamePingEvent() {
        attributes = Maps.newHashMap();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

}
