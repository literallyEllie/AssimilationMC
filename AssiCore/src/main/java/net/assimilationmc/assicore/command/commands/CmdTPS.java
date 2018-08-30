package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilMath;
import org.bukkit.command.CommandSender;

public class CmdTPS extends AssiCommand {

    public CmdTPS(AssiPlugin plugin) {
        super(plugin, "tps", "Shows current TPS of server", Rank.HELPER, Lists.newArrayList());
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        sender.sendMessage(C.C + "Average TPS: " + C.V + UtilMath.trim((float) plugin.getTpsTask().getAverage()));
    }

}
