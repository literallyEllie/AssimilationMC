package net.assimilationmc.assicore.chat;

import net.assimilationmc.assicore.player.AssiPlayer;

public class ChatMessage {

    private final AssiPlayer sender;
    private String format, message;
    private boolean cancelled;

    public ChatMessage(AssiPlayer sender, String format, String message) {
        this.sender = sender;
        this.format = format;
        this.message = message;
    }

    public AssiPlayer getSender() {
        return sender;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
