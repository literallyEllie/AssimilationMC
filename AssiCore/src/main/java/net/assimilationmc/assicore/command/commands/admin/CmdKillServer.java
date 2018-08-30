package net.assimilationmc.assicore.command.commands.admin;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.web.WebAPIManager;
import net.assimilationmc.assicore.web.request.RequestMethod;
import net.assimilationmc.assicore.web.request.WebEndpoint;
import org.bukkit.command.CommandSender;

public class CmdKillServer extends AssiCommand {

    public CmdKillServer(AssiPlugin plugin) {
        super(plugin, "killserver", "Forcefully kill a server by ID. " +
                "If server is not dead, it will be virtually disconnected from the rest of the network and could cause issues.", Rank.MANAGER, Lists.newArrayList(), "<server>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        final WebAPIManager webAPIManager = plugin.getWebAPIManager();
        if (!webAPIManager.isEnabled()) {
            sender.sendMessage(C.II + "There is no web server.");
            return;
        }

        sender.sendMessage(C.C + "Imitating a server shutdown process...");

        String serverId = args[0];

        // Send out hitman.
        webAPIManager.sendOneWayRequest(webAPIManager.defaultBuilder().setEndpoint(WebEndpoint.SERVER).setMethod(RequestMethod.EXPIRE_SERVER)
                .addParameter("name", serverId));

        // Imitate end message.
        plugin.getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.SPIGOT, serverId,
                "BYE", new String[]{"memes"}));

    }

}
