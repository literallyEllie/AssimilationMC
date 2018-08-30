package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by Ellie on 24/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Messages {

    public static JSONObject getList(String sessionId, int page, Object msgType, String keywords, String searchType, String searchArea, int limit, boolean unreadOnly,
                                     int lastTime, int afterTime) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Messages.getList").addParam("session_id", sessionId).addParam("page", page)
                .addParam("msg_type", msgType).addParam("unreadOnly", unreadOnly);
        if(!keywords.isEmpty()) requestBuilder.addParam("keywords", keywords);
        if(!searchType.isEmpty()) requestBuilder.addParam("searchType", keywords);
        if(!searchArea.isEmpty()) requestBuilder.addParam("searchArea", keywords);
        if (limit > 0) requestBuilder.addParam("limit", limit);
        if (lastTime > 0) requestBuilder.addParam("last_time", lastTime);
        if (afterTime > 0) requestBuilder.addParam("after_time", afterTime);
        return requestBuilder.build();
    }

    public static JSONObject getMessage(String sessionId, int pmId) {
        return new RequestBuilder().setMethod("Messages.getMessage").addParam("pm_id", pmId).addParam("session_id", sessionId).build();
    }

    public static JSONObject archiveMessage(String sessionId, int pmId) {
        return new RequestBuilder().setMethod("Messages.archiveMessage").addParam("pm_id", pmId).addParam("session_id", sessionId).build();
    }

    public static JSONObject sendMessage(String sessionId, String messageSubject, String messageBody, List<Object> recipients) {
        return new RequestBuilder().setMethod("Messages.sendMessage").addParam("message_subject", messageSubject)
                .addParam("message_body", messageBody).addParam("recipients", recipients).addParam("session_id", sessionId).build();
    }

    public static JSONObject sendReply(String sessionId, int pmId, String replyBody) {
        return new RequestBuilder().setMethod("Messages.sendReply").addParam("pm_id", pmId).addParam("reply_body", replyBody).addParam("session_id", sessionId).build();
    }

    public static JSONObject markRead(String sessionId, int pmId) {
        return new RequestBuilder().setMethod("Messages.markRead").addParam("pm_id", pmId).addParam("session_id", sessionId).build();
    }

    public static JSONObject markAllRead(String sessionId, Object msgType) {
        return new RequestBuilder().setMethod("Messages.markAllRead").addParam("msg_type", msgType).addParam("session_id", sessionId).build();
    }

    public static JSONObject markUnread(String sessionId, int pmId) {
        return new RequestBuilder().setMethod("Messages.markUnread").addParam("pm_id", pmId).addParam("session_id", sessionId).build();
    }

    public static JSONObject deleteMessages(String sessionId, Integer... pmIds) {
        return new RequestBuilder().setMethod("Messages.deleteMessages").addParam("pm_ids", pmIds).addParam("session_id", sessionId).build();
    }

    public static JSONObject deleteReply(String sessionId, int pmId) {
        return new RequestBuilder().setMethod("Messages.deleteReply").addParam("reply_id", pmId).addParam("session_id", sessionId).build();
    }

    public static JSONObject searchContacts(String sessionId, String search) {
        return new RequestBuilder().setMethod("Messages.searchContacts").addParam("search", search).addParam("session_id", sessionId).build();
    }

    public static JSONObject getComposeList(String sessionId) {
        return new RequestBuilder().setMethod("Messages.getComposeList").addParam("session_id", sessionId).build();
    }




}
