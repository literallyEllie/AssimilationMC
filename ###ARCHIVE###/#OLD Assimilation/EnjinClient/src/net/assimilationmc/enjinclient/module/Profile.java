package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 22/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Profile {

    public static JSONObject getProfile(String sessionId, int userId) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Profile.getProfile").addParam("session_id", sessionId);
        if(userId > 1) requestBuilder.addParam("user_id", userId);
        return requestBuilder.build();
    }

}
