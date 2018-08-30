package net.assimilationmc.assicore.world.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.SerializedLocation;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.assicore.world.WorldManager;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CmdSpawns extends AssiCommand {

    private WorldManager worldManager;

    public CmdSpawns(WorldManager worldManager) {
        super(worldManager.getPlugin(), "spawns", "Spawns for the world", Rank.ADMIN, Lists.newArrayList(),
                "<list | add | remove | tp>", "[spawnName]");
        this.worldManager = worldManager;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;
        World world = player.getWorld();

        switch (args[0].toLowerCase()) {
            case "list":
                sender.sendMessage(C.C + "All spawns for world " + C.V + world.getName() + C.C + ": ");
                for (Map.Entry<String, SerializedLocation> stringSerializedLocationEntry : worldManager.getWorldData(world.getName()).getSpawns().entrySet()) {
                    SerializedLocation serializedLocation = stringSerializedLocationEntry.getValue();
                    sender.sendMessage(C.V + stringSerializedLocationEntry.getKey() + C.C + ": " + serializedLocation.getX() + "," + serializedLocation.getY() +
                            "," + serializedLocation.getZ());
                }
                break;
            case "add":
                if (args.length < getRequiredArgs() + 1) {
                    usage(sender, usedLabel);
                    return;
                }
                String addMap = args[1];

                WorldData worldData = worldManager.getWorldData(player.getWorld());

                if (worldData.getSpawns().containsKey(addMap)) {
                    sender.sendMessage(C.II + "The spawn already exists with that name!");
                    return;
                }

                worldData.addSpawn(addMap, new SerializedLocation(player.getLocation(), true));
                sender.sendMessage(C.C + "Spawn added to world " + C.V + world.getName() + C.C + " with the name " + C.V + addMap + C.C + " to your position.");
                break;
            case "remove":
                if (args.length < getRequiredArgs() + 1) {
                    usage(sender, usedLabel);
                    return;
                }
                String removeMap = args[1];

                worldData = worldManager.getWorldData(world.getName());

                if (!worldData.getSpawns().containsKey(removeMap)) {
                    sender.sendMessage(C.II + "A spawn doesn't exists with that name!");
                    return;
                }

                worldData.removeSpawn(removeMap);
                sender.sendMessage(C.C + "Spawn removed from world " + C.V + world.getName() + C.C + " with by the name " + C.V + removeMap + C.C + ".");
                break;
            case "tp":
                if (args.length < getRequiredArgs() + 1) {
                    usage(sender, usedLabel);
                    return;
                }
                String tpMap = args[1];

                worldData = worldManager.getWorldData(world.getName());

                if (!worldData.getSpawns().containsKey(tpMap)) {
                    sender.sendMessage(C.II + "A spawn doesn't exists with that name!");
                    return;
                }

                player.teleport(worldData.getSpawns().get(tpMap).toLocation());
                sender.sendMessage(C.C + "Teleported to " + C.V + tpMap + C.C + ".");
                break;
            default:
                usage(sender, usedLabel);
        }
    }

}
