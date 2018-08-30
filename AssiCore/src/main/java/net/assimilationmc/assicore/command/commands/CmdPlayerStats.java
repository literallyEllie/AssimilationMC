package net.assimilationmc.assicore.command.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.command.CommandSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CmdPlayerStats extends AssiCommand {

    public CmdPlayerStats(AssiPlugin plugin) {
        super(plugin, "playerstats", "See your general stats", Lists.newArrayList("me"), "[player]");
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {

        AssiPlayer sender = null;
        if (isPlayer(commandSender))
            sender = asPlayer(commandSender);

        if (args.length < 1 && sender == null) {
            commandSender.sendMessage("Please specify the player to get the stats of.");
            return;
        }

        AssiPlayer target;

        if (args.length > 0 && (!isPlayer(commandSender) || (sender != null && sender.getRank().isHigherThanOrEqualTo(Rank.HELPER)))) {
            UUID uuid = plugin.getPlayerManager().getUUID(args[0]);

            if (uuid == null) {
                commandSender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[0] + C.C + " has ever joined the network.");
                return;
            }

            target = plugin.getPlayerManager().getOfflinePlayer(uuid);
        } else target = sender;

        if (target == null) {
            commandSender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[0] + C.C + " has ever joined the network.");
            return;
        }

        Date date = new Date(target.getFirstSeen());
        DateFormat format = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");

        commandSender.sendMessage(C.C + "General stats about " + C.V + target.getName() + C.C + ":");
        commandSender.sendMessage(C.C);
        commandSender.sendMessage(C.C + "First seen (dd/mm): " + C.V + format.format(date) + C.C + " | Last activity: " + C.V +
                UtilTime.formatTimeStamp(target.getLastSeen()));
        commandSender.sendMessage(C.C + "Last seen on: " + C.V + target.getLastSeenServer());
        commandSender.sendMessage(C.C + "Rank: " + C.V + (target.getRank().isDefault() ? "User" : target.getRank().getPrefix()));
        commandSender.sendMessage(C.C + "Recorded previous names: " + C.V + Joiner.on(C.C + ", " + C.V).join(target.getPreviousNames()));
        commandSender.sendMessage(C.C + "Bucks: " + C.BUCKS + target.getBucks() + "B " + C.C + "| Ultra Coins: " + C.UC + target.getUltraCoins() + "UC");
        commandSender.sendMessage(C.C + "Votes: " + C.V + target.getVotes() + C.C + " | Current vote streak: " + C.V + target.getVoteStreak());
        commandSender.sendMessage(C.C + "Referred by " + C.V + (target.getReferredBy().equals("NONE") ? "no-one" : target.getReferredBy()) + C.C + " | Joins: " + C.V + target.getJoins());
        commandSender.sendMessage(C.C + "Linked Discord account: " + C.V + (target.getDiscordAccount() == 0 ? "none" : target.getDiscordAccount()));
        if (!isPlayer(commandSender) || sender != null && sender.getRank().isHigherThanOrEqualTo(Rank.MOD)) {
            commandSender.sendMessage(C.II + "Current/Last IP: " + C.V + target.lastIP());
        }

    }

}
