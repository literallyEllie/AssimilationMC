package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 01/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Notifications {

    public static JSONObject getList(String sessionId, String filter, int olderThan) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Notifications.getList").addParam("session_id", sessionId);
        if(!filter.isEmpty()) requestBuilder.addParam("filter", filter);
        if (olderThan > 0) requestBuilder.addParam("older_than", olderThan);
        return requestBuilder.build();
    }

    public static JSONObject getTypes(String sessionId) {
        return new RequestBuilder().setMethod("Notifications.getTypes").addParam("session_id", sessionId).build();
    }

    public static JSONObject read(String sessionId, String type, int itemId) {
        return new RequestBuilder().setMethod("Notifications.read").addParam("session_id", sessionId).addParam("type", type)
                .addParam("item_id", itemId).build();
    }

    public static JSONObject readAll(String sessionId, String type) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Notifications.readAll").addParam("session_id", sessionId);
        if(!type.isEmpty()) requestBuilder.addParam("type", type);
        return requestBuilder.build();
    }

    public static JSONObject deleteAll(String sessionId, String type) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Notifications.deleteAll").addParam("session_id", sessionId);
        if(!type.isEmpty()) requestBuilder.addParam("type", type);
        return requestBuilder.build();
    }

    public static JSONObject setPushEnabled(String sessionId, String type, boolean enabled) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Notifications.setPushEnabled").addParam("session_id", sessionId).addParam("enabled", enabled);
        if(!type.isEmpty()) requestBuilder.addParam("type", type);
        return requestBuilder.build();
    }

    public static JSONObject getDisabledPushSettings(String sessionId) {
        return new RequestBuilder().setMethod("Notifications.getDisabledPushSettings").addParam("session_id", sessionId).build();
    }

    public static JSONObject getPushTypes(String sessionId) {
        return new RequestBuilder().setMethod("Notifications.getPushTypes").addParam("session_id", sessionId).build();
    }

}
