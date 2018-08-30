package net.assimilationmc.assicore.world.waypoint;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.world.WorldData;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CmdSpawnImport extends AssiCommand {

    public CmdSpawnImport(AssiPlugin plugin) {
        super(plugin, "spawnimport", "Import spawns from a VoxelMap .points file", Rank.ADMIN, Lists.newArrayList(), "<world>", "<file>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        File file = new File(plugin.getDataFolder(), args[1]);

        if (!file.exists()) {
            sender.sendMessage(C.II + "File not found, put it in the plugin data folder.");
            return;
        }

        WorldData worldData = plugin.getWorldManager().getWorldData(args[0]);
        if (worldData == null) {
            sender.sendMessage(C.II + "Bad world name.");
            return;
        }

        sender.sendMessage(C.C + "Imported " + C.V + new WayPointReader(worldData, file).read() + C.C + " spawns.");

    }

}
