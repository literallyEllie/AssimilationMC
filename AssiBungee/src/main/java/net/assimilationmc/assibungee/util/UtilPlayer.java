package net.assimilationmc.assibungee.util;

import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.UUID;

public class UtilPlayer {

    public static ProxiedPlayer get(String name) {
        return ProxyServer.getInstance().getPlayer(name);
    }

    public static ProxiedPlayer get(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public static String nameOrDefault(UUID sender, String fallback) {
        ProxiedPlayer player = get(sender);
        return player != null ? player.getName() : fallback;
    }

    public static BungeeGroup groupOf(CommandSender commandSender) {
        if (commandSender.getGroups().isEmpty()) return BungeeGroup.PLAYER;
        if (commandSender.getName().equals("CONSOLE")) return BungeeGroup.SUPERADMIN;

        for (String s : commandSender.getGroups()) {
            BungeeGroup group = BungeeGroup.fromString(s);
            if (group != BungeeGroup.PLAYER)
                return group;
        }

        return BungeeGroup.PLAYER;
    }


}
