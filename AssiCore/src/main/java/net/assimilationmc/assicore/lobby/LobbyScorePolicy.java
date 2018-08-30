package net.assimilationmc.assicore.lobby;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.commands.CmdVanish;
import net.assimilationmc.assicore.command.commands.CmdVerify;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.scoreboard.ScoreboardPolicy;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;

import java.util.List;

public class LobbyScorePolicy extends ScoreboardPolicy {

    public LobbyScorePolicy(AssiPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> getSideBar(AssiPlayer player) {
        final List<String> lines = Lists.newArrayList();

        lines.add(empty(0));

        lines.add(C.C + "Hello " + C.II + ChatColor.ITALIC + player.getName() + ChatColor.RESET + C.C + "!");

        lines.add(empty(1));

        lines.add(C.C + "Bucks: " + C.BUCKS + player.getBucks());
        lines.add(C.C + "Ultra Coins: " + C.UC + player.getUltraCoins());

        lines.add(empty(2));

        lines.add(C.C + "Lobby count: " + C.V + Math.max(UtilServer.getOnlinePlayers() - getPlugin().getPlayerManager().getOnlinePlayers().values()
                .stream().filter(AssiPlayer::isVanished).count(), 0));

        lines.add(empty(3));

        lines.add(C.C + "Rank:");
        lines.add((player.getRank().isDefault() ? ChatColor.GRAY + "Player" : player.getRank().getPrefix()));

        lines.add(empty(4));

        lines.add(C.V + Domain.WEB);

        return lines;
    }
}
