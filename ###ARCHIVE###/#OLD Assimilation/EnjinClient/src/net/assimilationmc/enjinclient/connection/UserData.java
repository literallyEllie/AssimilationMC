package net.assimilationmc.enjinclient.connection;

import net.assimilationmc.enjinclient.EnjinClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UserData {

    private String username;
    private int userID;
    private final String sessionID;
    private String partialLogin;

    private long lastSeen;

    private boolean favourite;
    private boolean online;

    private int pendingFriendRequests;
    private int mail;
    private int notifications;
    private int profileViews;

    private String location;
    private String friendType;

    private HashMap<String, EnjinSite> sites = new HashMap<>();

    public UserData(String username, int userid, String sessionID){
        this.username = username;
        this.userID = userid;
        this.sessionID = sessionID;
    }

    public UserData(int userId){
        this("undefined", userId, "undefined");
    }

    public UserData(String sessionID, String partialLogin){
        this("undefined", -1, sessionID);
        this.partialLogin = partialLogin;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPartialLogin() {
        return partialLogin;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public HashMap<String, EnjinSite> getSites() {
        return sites;
    }

    public void setSites(HashMap<String, EnjinSite> sites) {
        this.sites = sites;
    }

    public int getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public int getMail() {
        return mail;
    }

    public int getNotifications() {
        return notifications;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(int profileViews) {
        this.profileViews = profileViews;
    }

    public String getFriendType() {
        return friendType;
    }

    public void setFriendType(String friendType) {
        this.friendType = friendType;
    }

    void read(JSONObject result){

        if(result.containsKey("user_id")){
            this.userID = Integer.parseInt((String)result.get("user_id"));
        }

        if(result.containsKey("sites")){

            sites = new HashMap<>();
            for(Object object: (JSONArray) result.get("sites")){
                JSONObject jsonObject = (JSONObject) object;

                int access = Integer.parseInt((String)jsonObject.get("access"));
                String name = (String) jsonObject.get("name");
                String description = (String) jsonObject.get("description");
                String logo = (String) jsonObject.get("logo");
                String banner = (String) jsonObject.get("banner");
                boolean canJoin = (Boolean) jsonObject.get("can_join");
                String url = (String) jsonObject.get("url");
                int users = Integer.parseInt((String) jsonObject.get("users"));
                int likes = Integer.parseInt((String) jsonObject.get("likes"));
                boolean isLiked = (Boolean) jsonObject.get("is_liked");

                sites.put(name.toLowerCase(), new EnjinSite(name, description, url, logo, banner, canJoin, isLiked, access, users, likes));
                if(EnjinClient.debug) System.out.println("Added site "+name);
            }
        }

        if(result.containsKey("pending_friend_requests")){
            this.pendingFriendRequests = Integer.parseInt((String) result.get("pending_friend_requests"));
        }

        if(result.containsKey("mail")){
            this.mail = Integer.parseInt((String) result.get("mail"));
        }

        if(result.containsKey("notifications")){
            this.notifications = Integer.parseInt((String) result.get("notifications"));
        }

    }

}
