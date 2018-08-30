package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Friends {

    public static JSONObject getList(String sessionId, String type, int page, int limit, String token, String last_token, boolean online_only) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Friends.getList").
                addParam("type", type)
                .addParam("online_only", online_only).addParam("session_id", sessionId);
        if(page > -1) requestBuilder.addParam("page", page);
        if (limit > 0) requestBuilder.addParam("limit", limit);
        if(!token.isEmpty()) requestBuilder.addParam("token", token);
        if(!last_token.isEmpty()) requestBuilder.addParam("last_token", last_token);
        return requestBuilder.build();
    }

    public static JSONObject search(String sessionId, String query) {
        return new RequestBuilder().setMethod("Friends.search").addParam("text", query).addParam("session_id", sessionId).build();
    }

    public static JSONObject addFavourite(String sessionId, int friendId) {
        return new RequestBuilder().setMethod("Friends.addFavourite").addParam("friend_id", friendId).addParam("session_id", sessionId).build();
    }

    public static JSONObject removeFavourite(String sessionId, int friendId) {
        return new RequestBuilder().setMethod("Friends.removeFavourite").addParam("friend_id", friendId).addParam("session_id", sessionId).build();
    }

    public static JSONObject removeFriend(String sessionId, int friendId) {
        return new RequestBuilder().setMethod("Friends.removeFriend").addParam("friend_id", friendId).addParam("session_id", sessionId).build();
    }

    public static JSONObject sendRequest(String sessionId, int userId) {
        return new RequestBuilder().setMethod("Friends.sendRequest").addParam("user_id", userId).addParam("session_id", sessionId).build();
    }

    public static JSONObject acceptRequest(String sessionId, int userId) {
        return new RequestBuilder().setMethod("Friends.acceptRequest").addParam("user_id", userId).addParam("session_id", sessionId).build();
    }

    public static JSONObject acceptAllRequests(String sessionId) {
        return new RequestBuilder().setMethod("Friends.acceptAllRequests").addParam("session_id", sessionId).build();
    }

    public static JSONObject declineAllRequests(String sessionId) {
        return new RequestBuilder().setMethod("Friends.declineAllRequests").addParam("session_id", sessionId).build();
    }

    public static JSONObject ignoreRequest(String sessionId) {
        return new RequestBuilder().setMethod("Friends.ignoreRequest").addParam("session_id", sessionId).build();
    }

    public static JSONObject blockUser(String sessionId, int userId) {
        return new RequestBuilder().setMethod("Friends.blockUser").addParam("user_id", userId).addParam("session_id", sessionId).build();
    }

    public static JSONObject unblockUser(String sessionId, int userId) {
        return new RequestBuilder().setMethod("Friends.unblockUser").addParam("user_id", userId).addParam("session_id", sessionId).build();
    }

}
