package net.assimilationmc.uhclobbyadaptor.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilMath;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.stats.UHCPlayer;
import net.assimilationmc.uhclobbyadaptor.lib.GC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdStats extends AssiCommand {

    private final UHCLobbyAdaptor uhcLobbyAdaptor;

    public CmdStats(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(uhcLobbyAdaptor.getAssiPlugin(), "stats", "Shows your UHC stats", Lists.newArrayList("uhcstats"), "[player]");
        this.uhcLobbyAdaptor = uhcLobbyAdaptor;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] args) {
        Player sender = (Player) commandSender;

        Player target = null;
        if (args.length > 0) {

            target = UtilPlayer.get(args[0]);
            if (target == null) {
                couldNotFind(sender, args[0]);
                return;
            }

        }
        if (target == null) target = sender;

        UHCPlayer player = uhcLobbyAdaptor.getROuhcStatsProvider().getPlayer(target);

        sender.sendMessage("");
        sender.sendMessage(GC.II + "Stats about " + GC.V + player.getName() + ChatColor.GOLD + ":");
        sender.sendMessage(GC.C + "Level (XP): " + GC.V + player.getLevel() + C.C + " " + player.getXp() + "xp");
        sender.sendMessage(GC.C + "Kills/Deaths/KD: " + GC.V + player.getKills() + ChatColor.DARK_GRAY + "/" + GC.V + player.getDeaths()
                + ChatColor.DARK_GRAY + "/" + GC.V + UtilMath.trim(((float) player.getKills()) / (float) player.getDeaths()));
        sender.sendMessage(GC.C + "Games played/won: " + GC.V + player.getGamesPlayed() + C.C + "/" + GC.V + player.getWinCount());
        sender.sendMessage(GC.C + "Games stats: " + ChatColor.ITALIC + "(played/won)");
        player.getPreviousGamesPlayed().forEach((key, value) ->
                sender.sendMessage(GC.C + "   " + key.getDisplay() + GC.C + ": " + GC.V + value + C.C + "/" + GC.V + player.getGamesWon().
                        getOrDefault(key, 0)));

        if (player.getCooldownData() != null && player.getCooldownData().isActive()) {
            sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "On competitive cooldown.");
        }

//  sender.sendMessage(C.C + "Competitive rank:  " + player.getCompRank().getSuffix());


    }


}
