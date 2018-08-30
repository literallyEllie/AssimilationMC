package net.assimilationmc.assicore.punish.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;


public class CmdIPBan extends AssiCommand {

    public static String IP_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    public CmdIPBan(AssiPlugin plugin) {
        super(plugin, "ipban", "Override admin IP ban (Handle by bungee)", Rank.ADMIN, Lists.newArrayList(), "<ip>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String in = args[0];

        if (plugin.getServerData().isLocal()) {
            sender.sendMessage(C.II + "This command is disabled.");
            return;
        }

        if (!in.matches(IP_REGEX)) {
            sender.sendMessage(C.II + "Invalid IP.");
            return;
        }

        try (Jedis jedis = plugin.getRedisManager().getPool().getResource()) {
            final boolean contains = jedis.lrange("ip-bans", 0, -1).contains(in);
            if (contains) {
                sender.sendMessage(C.II + "Un-IP banning " + C.V + in + C.II + "... (may not have effect if other punishments)");
            } else {
                sender.sendMessage(C.II + "Override IP banning " + C.V + in + C.II + "...");
                plugin.getStaffChatManager().messageGlobalStaffChat(sender.getName(), " override IP-banned " + in);
            }
        }

        plugin.getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.PROXY,
                plugin.getServerData().getId(), "IP_BAN", new String[]{in, sender.getName()}));

    }

}
