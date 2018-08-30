package net.assimilationmc.assicore.command.commands.ambience;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdNight extends AssiCommand {

    public CmdNight(AssiPlugin plugin) {
        super(plugin, "night", "Set the time to night.", Rank.ADMIN, Lists.newArrayList("nighttime"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;
        player.getWorld().setFullTime(13000);
        player.sendMessage(C.C + "Time set to night.");
    }

}
