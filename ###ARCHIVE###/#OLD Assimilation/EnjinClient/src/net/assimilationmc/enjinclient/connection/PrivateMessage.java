package net.assimilationmc.enjinclient.connection;

/**
 * Created by Ellie on 24/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PrivateMessage {

    private int id;
    private int replies;
    private boolean networkPm;
    private boolean read;
    private String body;
    private String subject;
    private long time;

    private String sender;

    public PrivateMessage(int id, int replies, boolean networkPm, boolean read, String body, String subject, long time, String sender){
        this.id = id;
        this.replies = replies;
        this.networkPm = networkPm;
        this.read = read;
        this.body = body;
        this.subject = subject;
        this.time = time;
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public int getReplies() {
        return replies;
    }

    public boolean isNetworkPm() {
        return networkPm;
    }

    public boolean isRead() {
        return read;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public long getTime() {
        return time;
    }

    public String getSender() {
        return sender;
    }

}
