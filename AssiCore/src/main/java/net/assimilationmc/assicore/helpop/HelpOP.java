package net.assimilationmc.assicore.helpop;

import com.google.common.base.Joiner;
import net.assimilationmc.assicore.util.C;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;

import java.util.UUID;

public class HelpOP {

    private final int id;
    private UUID sender;
    private String senderOfflineName;
    private String server, content;
    private long sent;

    private String handler;

    /**
     * HelpOP
     *
     * @param id      HelpOP id
     * @param sender  Sender of the Helper
     * @param content HelpOP content.
     */
    public HelpOP(int id, UUID sender, String server, String content) {
        this.id = id;
        this.sender = sender;
        this.server = server;
        this.content = content;
        this.sent = System.currentTimeMillis();
    }

    /**
     * Deserialize a HelpOP (i.e from a pub-sub message)
     *
     * @param serialised The serialized form.
     */
    public HelpOP(String[] serialised) {
        this.id = Integer.parseInt(serialised[0]);
        this.sender = UUID.fromString(serialised[1]);
        this.senderOfflineName = serialised[2];
        this.server = serialised[3];
        this.sent = Long.parseLong(serialised[4]);
        this.handler = (serialised[5].equals("NONE") ? null : serialised[5]);

        String[] contentStr = new String[serialised.length - 6];
        System.arraycopy(serialised, 6, contentStr, 0, serialised.length - 6);
        this.content = Joiner.on(" ").join(contentStr);
    }

    /**
     * @return the HelpOP id.
     */
    public int getId() {
        return id;
    }

    /**
     * @return the uuid of the sender.
     */
    public UUID getSender() {
        return sender;
    }

    /**
     * @return the username of the sender.
     */
    public String getSenderOfflineName() {
        return senderOfflineName;
    }

    /**
     * Set the sender name.
     *
     * @param senderOfflineName The sender name.
     */
    public void setSenderOfflineName(String senderOfflineName) {
        this.senderOfflineName = senderOfflineName;
    }

    /**
     * @return the server where the HelpOPs comes from.
     */
    public String getServer() {
        return server;
    }

    /**
     * @return The content of the HelpOp.
     */
    public String getContent() {
        return content;
    }

    /**
     * @return The timestamp of when the HelpOp was sent.
     */
    public long getSent() {
        return sent;
    }

    /**
     * Set the helpOP to handled.
     *
     * @param handler the person who handled the HelpOP.
     */
    public void handle(String handler) {
        this.handler = handler;
    }

    /**
     * @return The handler of the HelpOp.
     */
    public String getHandler() {
        return handler;
    }

    /**
     * @return If the HelpOp is handled.
     */
    public boolean isHandled() {
        return handler != null;
    }

    /**
     * @return the pretty version of the HelpOP. (To send upon being received)
     */
    public BaseComponent[] pretty() {
        return new ComponentBuilder(ChatColor.DARK_PURPLE + ChatColor.STRIKETHROUGH.toString() +
                "---------- " + ChatColor.RED + " New HelpOP! " + C.V + "#" + C.V + id + " " + ChatColor.DARK_PURPLE + ChatColor.STRIKETHROUGH.toString() + "----------\n"
                + C.C + "Sender: " + C.V + senderOfflineName + "\n"
                + C.C + "Server: " + C.V + server + "\n"
                + C.C + "Content: " + C.V + content.replace("\"", "") + "\n"
                + ChatColor.DARK_PURPLE + ChatColor.STRIKETHROUGH.toString() + "---------------------------------")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop handle " + id))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to handle this HelpOP").
                        color(net.md_5.bungee.api.ChatColor.RED).create()))
                .create();
    }

    /**
     * @return serialize the HelpOp ready for sending over stuff like Redis.
     */
    public String[] serialise() {
        return new String[]{String.valueOf(id), sender.toString(), senderOfflineName, server, String.valueOf(sent), (handler == null ? "NONE" : handler),
                content};
    }

}
