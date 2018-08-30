package net.assimilationmc.assicore.command.commands.admin;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.web.WebAPIManager;
import net.assimilationmc.assicore.web.request.RequestMethod;
import net.assimilationmc.assicore.web.request.WebEndpoint;
import org.bukkit.command.CommandSender;

public class CmdGRAMUse extends AssiCommand {

    public CmdGRAMUse(AssiPlugin plugin) {
        super(plugin, "gramuse", "See recorded global RAM usage by automated managed servers.", Rank.ADMIN, Lists.newArrayList());
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        final WebAPIManager webAPIManager = plugin.getWebAPIManager();
        if (!webAPIManager.isEnabled()) {
            sender.sendMessage(C.II + "There is no web server.");
            return;
        }

        webAPIManager.sendRequest(webAPIManager.defaultBuilder().setEndpoint(WebEndpoint.SERVER)
                .setMethod(RequestMethod.USAGE), data -> sender.sendMessage(C.II + "Global managed RAM: " + C.V + data.getDetailedMessage() + "GB"));
    }


}
