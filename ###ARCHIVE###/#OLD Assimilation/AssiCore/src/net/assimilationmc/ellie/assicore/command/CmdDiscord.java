package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by Ellie on 8.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdDiscord extends AssiCommand {

    public CmdDiscord() {
        super("discord", "discord", "Link for the discord server", Arrays.asList());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sendPMessage(sender, "You can find our discord at " + ColorChart.VARIABLE + "https://discord.gg/bhyDjPJ");
    }

}
