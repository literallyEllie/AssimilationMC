package net.assimilationmc.assicore.world.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * @author Ellie :: 17/07/2018
 */
public class CmdUnloadWorld extends AssiCommand {

    public CmdUnloadWorld(AssiPlugin plugin) {
        super(plugin, "unloadworld", "Loads a world", Rank.ADMIN, Lists.newArrayList(), "<world> [-nosave]");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String world = args[0];

        for (World bWorld : Bukkit.getWorlds()) {
            if (bWorld.getName().equalsIgnoreCase(world)) {

                boolean save = true;
                if (args.length > 1) {
                    save = args[1].toLowerCase().contains("nosave");
                }

                bWorld.getPlayers().forEach(player -> player.performCommand("spawn"));

                Bukkit.unloadWorld(bWorld, save);
                sender.sendMessage(C.II + "Unloaded.");

                return;
            }
        }

        couldNotFind(sender, world);
    }

}
