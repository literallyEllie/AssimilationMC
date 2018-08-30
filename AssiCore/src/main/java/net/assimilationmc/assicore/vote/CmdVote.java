package net.assimilationmc.assicore.vote;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class CmdVote extends AssiCommand {

    public CmdVote(AssiPlugin plugin) {
        super(plugin, "vote", "Vote for us for rewards!", Lists.newArrayList("votes", "voteStreak"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        AssiPlayer sender = asPlayer(commandSender);

        sender.sendMessage(prefix(usedLabel) + "All vote links:");
        plugin.getVoteManager().getVoteLinks().forEach(s -> sender.sendMessage(C.SS + C.V + s));
        sender.sendMessage(C.C);
        sender.sendMessage(C.C + "You have " + C.V + sender.getVotes() + C.C + " votes. " +
                (sender.hasVoteStreak() ? "And a current vote streak of " + C.V + sender.getVoteStreak() + C.C + " days!" : "With no current vote streak."));

        long lastVote = sender.getLastVote();
        if (lastVote != 0) {
            if (UtilTime.elapsed(lastVote, TimeUnit.DAYS.toMillis(1))) {
                sender.sendMessage(C.II + "The time to vote is now!" + C.C + " You only have 48 hours after you vote to vote again to " + (
                        sender.hasVoteStreak() ? "keep your" : "start a") + " streak." + " Time is ticking!");
            }
        }

    }

}
