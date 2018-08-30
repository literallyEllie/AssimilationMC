package net.assimilationmc.assicore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilPlayer {

    /**
     * "Shorthand" player getter
     *
     * @param name Player name to get
     * @return Player from {@link Bukkit#getPlayer(String)}. Could be null.
     */
    public static Player get(String name) {
        return Bukkit.getPlayer(name);
    }

    /**
     * "Shorthand" player getter
     *
     * @param uuid Player UUID to get
     * @return Player from {@link Bukkit#getPlayer(UUID)}. Could be null.
     */
    public static Player get(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Kick all online players for a specified reason
     *
     * @param reason Reason to display.
     */
    public static void kickAll(String reason) {
        Bukkit.getOnlinePlayers().forEach(o -> o.kickPlayer(reason));
    }

    public static String nameOrDefault(UUID uuid, String fallback) {
        final Player player = get(uuid);
        return player.isOnline() ? player.getName() : fallback;
    }

    public static List<String> filterPlayers(String prefix) {
        return Bukkit.getOnlinePlayers().stream().filter(o -> o.getName().toLowerCase().startsWith(prefix.toLowerCase())).map(Player::getName)
                .collect(Collectors.toList());
    }

    /**
     * Get the player ip
     *
     * @param player the player to get the IP of
     * @return the player IP.
     */
    public static String getIP(Player player) {
        return player.getAddress().getAddress().getHostAddress();
    }


}
