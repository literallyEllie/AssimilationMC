package net.assimilationmc.assicore.world.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.world.AssiRegion;
import net.assimilationmc.assicore.world.LocationSelector;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.assicore.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CmdRegions extends AssiCommand {

    private WorldManager worldManager;

    public CmdRegions(WorldManager worldManager) {
        super(worldManager.getPlugin(),
                "regions", "World region management", Rank.ADMIN, Lists.newArrayList(), "<list | add | remove | tp [max/min]>", "[regionName]");
        this.worldManager = worldManager;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;
        World world = player.getWorld();

        switch (args[0].toLowerCase()) {
            case "list":
                sender.sendMessage(C.C + "All regions for world " + C.V + world.getName() + C.C + ": ");
                for (Map.Entry<String, AssiRegion> stringAssiRegionEntry : worldManager.getWorldData(world.getName()).getRegions().entrySet()) {
                    AssiRegion assiRegion = stringAssiRegionEntry.getValue();


                    sender.sendMessage(C.V + stringAssiRegionEntry.getKey() + C.C + ": (min = " + C.V + assiRegion.getMinX() + C.C + "/" + C.V +
                            assiRegion.getMinY() + C.C + "/" + C.V + assiRegion.getMinZ() + C.C + ") (max = " + C.V + assiRegion.getMaxX() + C.C + "/" +
                            C.V + assiRegion.getMaxY() + C.C + "/" + C.V + assiRegion.getMaxZ() + C.C + ")");
                }
                break;
            case "add":
                if (args.length < getRequiredArgs() + 1) {
                    usage(sender, usedLabel);
                    return;
                }
                String addRegion = args[1];

                WorldData worldData = worldManager.getWorldData(world.getName());

                if (worldData.getRegions().containsKey(addRegion)) {
                    sender.sendMessage(C.II + "A region already exists with that name!");
                    return;
                }

                new LocationSelector(plugin, player, data -> {
                    AssiRegion assiRegion = new AssiRegion(data.getKey(), data.getValue());
                    worldData.addRegion(addRegion, assiRegion);
                    sender.sendMessage(C.C + "Region added to world " + C.V + world.getName() + C.C +
                            " with the name " + C.V + addRegion + C.C + " to your locations you set.");
                });
                break;
            case "remove":
                if (args.length < getRequiredArgs() + 1) {
                    usage(sender, usedLabel);
                    return;
                }
                String removeRegion = args[1];

                worldData = worldManager.getWorldData(world.getName());

                if (!worldData.getRegions().containsKey(removeRegion)) {
                    sender.sendMessage(C.II + "A region doesn't exists with that name!");
                    return;
                }

                worldData.removeRegion(removeRegion);
                sender.sendMessage(C.C + "Region removed from world " + C.V + world.getName() + C.C + " with by the name " + C.V + removeRegion + C.C + ".");
                break;
            case "tp":
                if (args.length < getRequiredArgs() + 2) {
                    usage(sender, usedLabel);
                    return;
                }

                String maxMin = args[1].toLowerCase();
                if (!(maxMin.equals("max") || maxMin.equals("min"))) {
                    usage(sender, usedLabel);
                    return;
                }

                String tpRegion = args[2];

                worldData = worldManager.getWorldData(world.getName());

                if (!worldData.getSpawns().containsKey(tpRegion)) {
                    sender.sendMessage(C.II + "A region doesn't exists with that name!");
                    return;
                }

                final AssiRegion assiRegion = worldData.getRegions().get(tpRegion);

                switch (maxMin) {
                    case "max":
                        player.teleport(new Location(world, assiRegion.getMaxX(), assiRegion.getMaxY(), assiRegion.getMaxZ()));
                        break;
                    case "min":
                        player.teleport(new Location(world, assiRegion.getMinX(), assiRegion.getMinY(), assiRegion.getMinZ()));
                        break;
                }

                sender.sendMessage(C.C + "Teleported to the " + C.V + maxMin + C.C + " location of " + C.V + tpRegion + C.C + ".");
                break;
            default:
                usage(sender, usedLabel);

        }

    }

}