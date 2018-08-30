package net.assimilationmc.assicore.parkour;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdParkour extends AssiCommand {

    private final ParkourManager manager;

    public CmdParkour(ParkourManager manager) {
        super(manager.getPlugin(), "parkour", "Parkour", Lists.newArrayList("pk"));
        this.manager = manager;
    }

    @Override
    public void onCommand(CommandSender console, String usedLabel, String[] args) {
        final Map<UUID, ParkourPlayer> participants = manager.getParkourPlayerMap();

        if (args.length > 0) {

            String action = args[0];

            if (isPlayer(console) && action.equalsIgnoreCase("addchkp")) {

                manager.addCheckpoint(((Player) console).getLocation());
                console.sendMessage(manager.getPrefix() + "Added a checkpoint to your location.");
                return;
            }

            if (isPlayer(console) && action.equalsIgnoreCase("best")) {
                final ParkourPlayer player = manager.getPlayer((Player) console);
                if (player != null && player.getPersonalBest() != 0) {
                    console.sendMessage(manager.getPrefix() + ChatColor.GOLD + "Your personal best: " + C.V + TimeUnit.MILLISECONDS.toSeconds(player.getPersonalBest()) + "s");
                } else console.sendMessage(manager.getPrefix() + C.C + "You have not completed the course yet.");

                return;
            }

            Player target = null;
            ParkourPlayer parkourPlayer;
            if (args.length > 1 && (!isPlayer(console) || asPlayer(console).getRank().isHigherThanOrEqualTo(Rank.ADMIN))) {

                target = UtilPlayer.get(args[1]);
                if (target == null) {
                    couldNotFind(console, args[1]);
                    return;
                }
            }

            if (args.length == 1 && !isPlayer(console)) {
                return;
            }

            if (target == null) target = (Player) console;

            if (action.equalsIgnoreCase("start")) {
                manager.startParkour(target);
                return;
            } else if (action.equalsIgnoreCase("restart")) {
                parkourPlayer = manager.getPlayer(target);

                if (!parkourPlayer.isRunning()) {
                    target.sendMessage(manager.getPrefix() + ChatColor.RED + "You are not running!");
                    return;
                }

                manager.restart(parkourPlayer);
                return;
            } else if (action.equalsIgnoreCase("stop")) {
                parkourPlayer = manager.getPlayer(target);

                if (parkourPlayer == null || !parkourPlayer.isRunning()) {

                    if (target == console) {
                        target.sendMessage(manager.getPrefix() + ChatColor.RED + "You are not running!");
                        return;
                    }

                    if (isPlayer(console))
                        console.sendMessage(manager.getPrefix() + ChatColor.RED + "That player is not running.");
                    return;
                }

                manager.finishParkour(target, parkourPlayer, !isPlayer(console));
                return;
            }

        }

        if (isPlayer(console)) {
            Player player = (Player) console;

            ParkourPlayer parkourPlayer = participants.get(player.getUniqueId());
            if (parkourPlayer != null && parkourPlayer.isRunning()) {
                player.sendMessage(manager.getPrefix() + ChatColor.GOLD + ChatColor.BOLD + "You are currently running! " +
                        C.C + "If you want to stop, do " + C.V + "/parkour stop" + C.C + " or " + C.V + "/parkour restart");
                return;
            }

            player.sendMessage(manager.getPrefix() + "Command usage:");
            player.sendMessage(C.V + "/parkour start " + ChatColor.DARK_GRAY + ChatColor.GREEN + "Start the course");
            player.sendMessage(C.V + "/parkour best " + ChatColor.DARK_GRAY + ChatColor.GREEN + "See your personal best");
        }
    }


}
