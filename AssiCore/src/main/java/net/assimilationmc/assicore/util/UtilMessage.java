package net.assimilationmc.assicore.util;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class UtilMessage {

    /**
     * Send a player a hot bar message.
     * If you want this to persist you must send on a task.
     *
     * @param player  the player to send it to.
     * @param message the message to send.
     */
    public static void sendHotbar(Player player, String message) {
        final PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat();
        UtilReflect.setValue(packetPlayOutChat, "a", IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\",\"color\":\"white\"}"));
        UtilReflect.setValue(packetPlayOutChat, "b", (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    /**
     * Send custom pretty tab list.
     *
     * @param player Player to give tab-list to.
     * @param header The tab header.
     * @param footer The tab footer.
     */
    public static void sendTab(Player player, String header, String footer) {
        final CraftPlayer craftplayer = (CraftPlayer) player;
        final PlayerConnection connection = craftplayer.getHandle().playerConnection;
        final IChatBaseComponent hj = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        final IChatBaseComponent fj = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            final Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, hj);
            headerField.setAccessible(!headerField.isAccessible());

            final Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, fj);
            footerField.setAccessible(!footerField.isAccessible());
        } catch (Exception ignored) {
        }

        connection.sendPacket(packet);
    }

    /**
     * Send a player a title message.
     *
     * @param player  the player to send it to.
     * @param title   the title to display.
     * @param fadeIn  how long it will take to fade in (in ticks)
     * @param persist how long it will stay on the screen (in ticks)
     * @param fadeOut how long it will take to fade out (in ticks)
     */
    public static void sendTitle(Player player, String title, int fadeIn, int persist, int fadeOut) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        PacketPlayOutTitle playOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatBaseComponent);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, persist, fadeOut);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    /**
     * Send a player a title message with takes 5 ticks to fade in and out and stays for 1 second (20 ticks)
     *
     * @param player the player to send it to.
     * @param title  the title.
     */
    public static void sendTitle(Player player, String title) {
        sendTitle(player, title, 5, 20, 5);
    }

    /**
     * Send everyone on the server a title that stays for 1 second.
     *
     * @param title the title to send.
     */
    public static void sendAllTitle(String title) {
        Bukkit.getOnlinePlayers().forEach(o -> sendTitle(o, title));
    }

    /**
     * Send a player a sub-title message.
     *
     * @param player   the player to send it to.
     * @param subTitle the subtitle to display.
     * @param fadeIn   how long it will take to fade in (in ticks)
     * @param persist  how long it will stay on the screen (in ticks)
     * @param fadeOut  how long it will take to fade out (in ticks)
     */
    public static void sendSubTitle(Player player, String subTitle, int fadeIn, int persist, int fadeOut) {
        sendFullTitle(player, "", subTitle, fadeIn, persist, fadeOut);
    }

    /**
     * Send a player a subtitle message with takes 5 ticks to fade in and out and stays for 1 second (20 ticks)
     *
     * @param player   the player to send it to.
     * @param subtitle the subtitle.
     */
    public static void sendSubTitle(Player player, String subtitle) {
        sendTitle(player, subtitle, 5, 20, 5);
    }

    /**
     * Send everyone on the server a subtitle that stays for 1 second.
     *
     * @param subtitle the subtitle to send.
     */
    public static void sendAllSubTitle(String subtitle) {
        Bukkit.getOnlinePlayers().forEach(o -> sendSubTitle(o, subtitle));
    }

    /**
     * Send a player a title and sub-title message.
     *
     * @param player   the player to send it to.
     * @param title    the title to display.
     * @param subTitle the subtitle to display.
     * @param fadeIn   how long it will take to fade in (in ticks)
     * @param persist  how long it will stay on the screen (in ticks)
     * @param fadeOut  how long it will take to fade out (in ticks)
     */
    public static void sendFullTitle(Player player, String title, String subTitle, int fadeIn, int persist, int fadeOut) {
        IChatBaseComponent titleChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent subChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");
        PacketPlayOutTitle titlePlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleChatBaseComponent);
        PacketPlayOutTitle subTitlePlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subChatBaseComponent);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, persist, fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePlayOutTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitlePlayOutTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    /**
     * Send a player a title and subtitle message with takes 5 ticks to fade in and out and stays for 1 second (20 ticks)
     *
     * @param player   the player to send it to.
     * @param title    the title.
     * @param subtitle the subtitle.
     */
    public static void sendFullTitle(Player player, String title, String subtitle) {
        sendFullTitle(player, title, subtitle, 5, 20, 5);
    }

    /**
     * Send everyone on the server a title and a subtitle that stays for 1 second.
     *
     * @param title    the title to send.
     * @param subtitle the subtitle to send.
     */
    public static void sendAllSubTitle(String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(o -> sendFullTitle(o, title, subtitle));
    }

    /**
     * Send a progress bar to a player.
     *
     * @param player   player to send to
     * @param prefix   prefix of the bar.
     * @param current  current progress.
     * @param required required progress until completion.
     */
    public static void sendProgressActionBar(Player player, String prefix, int current, int required) {
        final float progress = UtilMath.percentage(current, required);

        final StringBuilder bar = new StringBuilder(ChatColor.DARK_GRAY + "[");
        final int barLength = 20;

        for (int i = 0; i < barLength; i++) {
            if (i < barLength * progress) {
                bar.append(ChatColor.GREEN).append("|");
            } else {
                bar.append(ChatColor.RED).append(":");
            }
        }

        bar.append(ChatColor.DARK_GRAY).append("]");
        sendHotbar(player, prefix + " " + bar.toString() + " " + C.C + ChatColor.ITALIC + UtilMath.trim(progress * 100) + "%");
    }

}

