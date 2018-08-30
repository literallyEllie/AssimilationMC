package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * Created by Ellie on 8.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdWebsite extends AssiCommand {

    public CmdWebsite() {
        super("website", "website", "Link for the website", Collections.singletonList("web"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sendPMessage(sender, "You can find our website at " + ColorChart.VARIABLE + "https://forums.assimilationmc.net/");
    }

}
