package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.redis.RedisDatabaseIndex;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class CmdPurgeRedis extends BungeeCommand {

    public CmdPurgeRedis(AssiBungee assiBungee) {
        super(assiBungee, "purgeredis", BungeeGroup.SUPERADMIN, Lists.newArrayList(), "<uuid>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(new ComponentBuilder("Invalid UUID").color(ChatColor.RED).create());
            return;
        }

        final ProxiedPlayer player = UtilPlayer.get(uuid);
        if (player != null) {
            player.disconnect(new ComponentBuilder("Please reconnect.\nDispatcher: " + sender.getName()).color(ChatColor.RED).create());
        }

        plugin.getRedisManager().del(RedisDatabaseIndex.DATA_USERS, plugin.getPlayerManager().redisKey(args[0]));
        sender.sendMessage(new ComponentBuilder("Redis data purged").color(ChatColor.RED).create());

    }

}
