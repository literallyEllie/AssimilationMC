package net.assimilationmc.assicore.command.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class CmdList extends AssiCommand {

    public CmdList(AssiPlugin plugin) {
        super(plugin, "list", "Show a list of the online players", Lists.newArrayList("online"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        AssiPlayer player = asPlayer(sender);

        final List<AssiPlayer> allOnline = (player.getRank().isHigherThanOrEqualTo(Rank.DEVELOPER) ?
                Lists.newArrayList(plugin.getPlayerManager().getOnlinePlayers().values()) :
                plugin.getPlayerManager().getOnlinePlayers().values().stream().filter(AssiPlayer::isVanished).collect(Collectors.toList()));

        player.sendMessage(prefix(usedLabel) + "Online players: (" + C.V + allOnline.size() + C.C + ")");
        player.sendMessage(Joiner.on(C.C + ", " + C.V).join(allOnline.stream().map(AssiPlayer::getDisplayName).collect(Collectors.toList())));
    }


}
