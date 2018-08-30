package net.assimilationmc.assicore.command.commands.ambience;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSun extends AssiCommand {

    public CmdSun(AssiPlugin plugin) {
        super(plugin, "sun", "Set the weather to sunny.", Rank.ADMIN, Lists.newArrayList("toggledownfall", "norain",
                "rainraingoaway"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;

        player.getWorld().setThunderDuration(0);
        player.getWorld().setStorm(false);
        player.getWorld().setWeatherDuration(10000);
        player.sendMessage(C.C + "Weather set to sunny.");
    }


}
