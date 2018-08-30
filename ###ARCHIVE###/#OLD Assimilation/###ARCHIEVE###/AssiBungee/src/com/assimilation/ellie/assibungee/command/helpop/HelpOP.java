package com.assimilation.ellie.assibungee.command.helpop;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class HelpOP {

    private final int id, ssid;
    private final String sender, content, server;
    private final long sent;
    private String handler;
    private boolean handled;

    public HelpOP(int id, int ssid, String sender, String server, String content, long sent){
        this.id = id;
        this.ssid = ssid;
        this.sender = sender;
        this.server = server;
        this.content = content;
        this.sent = sent;
        this.handled = false;
    }

    public int getID() {
        return id;
    }

    public int getSsid() {
        return ssid;
    }

    public String getSender() {
        return sender;
    }

    public String getServer() {
        return server;
    }

    public String getContent() {
        return content;
    }

    public String getHandler() {
        return handler;
    }

    public void handle(String handler){
        this.handler = handler;
        this.handled = true;
    }

    public long getSent() {
        return sent;
    }

    public boolean isHandled() {
        return handled;
    }

    public String toString(){
        return "#"+id;


    }

}
