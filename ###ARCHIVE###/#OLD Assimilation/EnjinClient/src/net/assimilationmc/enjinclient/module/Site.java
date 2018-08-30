package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 22/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Site {

    public static JSONObject getSiteInfo(String sessionId, int siteId) {
        return new RequestBuilder().setMethod("Site.getSiteInfo").addParam("session_id", sessionId).addParam("site_id", siteId).build();
    }

    public static JSONObject getStats(String sessionId, int siteId) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Site.getStats").addParam("session_id", sessionId);
        if(siteId > 1) requestBuilder.addParam("site_id", siteId);
        return requestBuilder.build();
    }

    public static JSONObject join(String sessionId, int siteId, int recaptcha_response) {
        return new RequestBuilder().setMethod("Site.join").addParam("session_id", sessionId).addParam("recaptcha_response", recaptcha_response).addParam("site_id", siteId).build();
    }

    public static JSONObject leave(String sessionId, int siteId, String password) {
        return new RequestBuilder().setMethod("Site.leave").addParam("session_id", sessionId).addParam("site_id", siteId).addParam("password", password).build();
    }

    public static JSONObject likeSite(String sessionId, int siteId, String state) {
        return new RequestBuilder().setMethod("Site.likeSite").addParam("session_id", sessionId).addParam("site_id", siteId).addParam("type", state).build();
    }

}
