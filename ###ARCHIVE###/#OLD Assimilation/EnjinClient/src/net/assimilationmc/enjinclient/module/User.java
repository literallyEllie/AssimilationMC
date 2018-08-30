package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class User {

    public static JSONObject login(String email, String password) {
        return new RequestBuilder().setMethod("User.login").addParam("email", email).addParam("password", password).build();
    }

    public static JSONObject checkSession(String sessionId) {
        return new RequestBuilder().setMethod("User.checkSession").addParam("session_id", sessionId).build();
    }

    public static JSONObject getUserSites(int userId, boolean includeBanned) {
        return new RequestBuilder().setMethod("User.getUserSites").addParam("user_id", userId).addParam("include_banned", includeBanned).build();
    }

    public static JSONObject authySMS(String userPartialLogin) {
        return new RequestBuilder().setMethod("User.authySMS").addParam("user_partial_login", userPartialLogin).build();
    }

    public static JSONObject authyVerify(String code, String userPartialLogin) {
        return new RequestBuilder().setMethod("User.authyVerify").addParam("authy_code", code)
                .addParam("user_partial_login", userPartialLogin).build();
    }

    public static JSONObject forgotPassword(String email) {
        return new RequestBuilder().setMethod("User.forgotPassword").addParam("email", email).build();
    }

    public static JSONObject getPusherChannels() {
        return new RequestBuilder().setMethod("User.getPusherChannels").build();
    }

    public static JSONObject get(String sessionId) {
        return new RequestBuilder().setMethod("User.get").addParam("session_id", sessionId).build();
    }

    public static JSONObject getWarnings(String sessionId, int siteId) {
        return new RequestBuilder().setMethod("User.getWarnings").addParam("session_id", sessionId).addParam("site_id", siteId).build();
    }

    public static JSONObject acknowledgeWarning(String sessionId, int siteId, Object issuedWarningRid, int issuedWarningId) {
        return new RequestBuilder().setMethod("User.acknowledgeWarning").addParam("site_id", siteId)
                .addParam("issued_warning_rid ", issuedWarningRid).addParam("session_id", sessionId).addParam("issued_warning_id ", issuedWarningId).build();
    }

    public static JSONObject searchMentionableUsers(String sessionId, Object query) {
        return new RequestBuilder().setMethod("User.searchMentionableUsers").addParam("session_id", sessionId).addParam("query", query).build();
    }


}
