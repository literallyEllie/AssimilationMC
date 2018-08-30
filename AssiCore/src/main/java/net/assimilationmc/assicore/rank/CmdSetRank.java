package net.assimilationmc.assicore.rank;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdSetRank extends AssiCommand {

    public CmdSetRank(AssiPlugin plugin) {
        super(plugin, "setrank", "Set player rank", Rank.ADMIN, Lists.newArrayList(), "<rank>", "<user>");
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {

        AssiPlayer sender = null;
        if (isPlayer(commandSender))
            sender = asPlayer(commandSender);

        final Rank rank = Rank.fromString(args[0]);

        if (rank == null) {
            commandSender.sendMessage(C.II + "Rank invalid.");
            return;
        }

        if (isPlayer(commandSender) && sender != null && !sender.getRank().isHigherThanOrEqualTo(rank)) {
            sender.sendMessage(C.II + "You cannot set a rank higher than your own.");
            return;
        }

        UUID uuid = plugin.getPlayerManager().getUUID(args[1]);

        if (uuid == null) {
            commandSender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[1] + C.C + " has ever joined the network.");
            return;
        }

        AssiPlayer target = plugin.getPlayerManager().getOfflinePlayer(uuid);

        if (target.getRank().equals(rank)) {
            commandSender.sendMessage(C.II + "Their rank is already set to " + C.V + rank.getName() + C.II + ".");
            return;
        }

        // discord log
        plugin.getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.PROXY, plugin.getServerData().getId(),
                "RANK_CHANGE", new String[]{target.getName(), target.getRank().getName(), rank.getName(),
                (isPlayer(sender) ? "[" + asPlayer(sender).getRank().getName() + "] " : "") + commandSender.getName() + " dispatched @ " +
                        plugin.getServerData().getId()}));

        target.setRank(rank);

        if (isPlayer(commandSender))
            commandSender.sendMessage(C.II + "Updated rank of " + target.getDisplayName() + C.II + " to " + rank.getPrefix() + C.II + ".");

        plugin.getLogger().info("[RANK CHANGE] " + commandSender.getName() + " set rank of " + target.getName() + " to " + rank.getName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = Lists.newArrayList();

        if (args.length == 1) {
            completions.addAll(Arrays.stream(Rank.values()).filter(rank -> rank.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .filter(rank -> isPlayer(sender) && !rank.isHigherThanOrEqualTo(asPlayer(sender).getRank())).map(Rank::getName)
                    .collect(Collectors.toList()));
        }

        if (args.length == 2) {
            completions.addAll(UtilPlayer.filterPlayers(args[1]));
        }

        return completions;
    }

}
