package net.assimilationmc.assicore.chat;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import org.bukkit.ChatColor;

public class ChatPolicy {

    private String format;

    private int chatDelay;
    private Rank requiredRankChat;

    /**
     * A class which other chat policies must extend to have effect on how messages are processed on the server.
     * In the constructor it sets the default chat format. If you wish to add your own placeholders, you must process them yourself.
     * It also disables any chat delay and turns off any chat rank requirement.
     */
    public ChatPolicy() {
        this.format = "{display}" + ChatColor.DARK_GRAY + " » {message}";
        this.chatDelay = -1;
        this.requiredRankChat = Rank.PLAYER;
    }

    /**
     * A method to allow the chat policy to do any changes to the chat before its processed.
     *
     * @param message the message to handle.
     * @return the updated message.
     */
    public ChatMessage handleChat(ChatMessage message) {
        AssiPlayer player = message.getSender();
        message.setFormat(format.replace("{message}",
                (player.getOverrideChatColor() == null ? player.getRank().isDefault() ? ChatColor.GRAY : ChatColor.RESET :
                        player.getOverrideChatColor()) + "%2$s"));
        return message;
    }

    /**
     * @return the format to format player messages into when they chat.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the format that messages will be formatted to when they speak.
     *
     * @param format the format to set it to.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the chat delay of the chat.
     * If the delay is over 0, it will return true, as there is a delay.
     * If the delay is less than 1, it will return false.
     */
    public boolean hasChatDelay() {
        return chatDelay > 0;
    }

    /**
     * @return the chat delay of the chat.
     */
    public int getChatDelay() {
        return chatDelay;
    }

    /**
     * Set the chat delay.
     *
     * @param chatDelay the delay of the chat. Setting it to below 1 will effectively disable it.
     */
    public void setChatDelay(int chatDelay) {
        this.chatDelay = chatDelay;
    }

    /**
     * @return if there is restricted chat.
     * It will return true if the requiredRankChat is not a default rank.
     */
    public boolean isRestrictedChat() {
        return !this.requiredRankChat.isDefault();
    }

    /**
     * @return the current rank required to speak.
     */
    public Rank getRequiredRankChat() {
        return requiredRankChat;
    }

    /**
     * Set the required rank to speak in chat.
     *
     * @param requiredRankChat the required rank, if null it will be turned off.
     */
    public void setRequiredRankChat(Rank requiredRankChat) {
        if (requiredRankChat == null)
            requiredRankChat = Rank.PLAYER;
        this.requiredRankChat = requiredRankChat;
    }

}
