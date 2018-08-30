package net.assimilationmc.assicore.command.commands.eco;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdBalance extends AssiCommand {

    public CmdBalance(AssiPlugin plugin) {
        super(plugin, "balance", "Shows your or another player's balance.", Lists.newArrayList(
                "bal", "money", "dosh", "bucks", "ultracoins", "coins"), "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        AssiPlayer player = asPlayer(sender);

        if (args.length > 0 && player.getRank().isHigherThanOrEqualTo(Rank.HELPER)) {
            Player target = UtilPlayer.get(args[0]);
            if (target == null) {
                couldNotFind(sender, args[0]);
                return;
            }

            player = plugin.getPlayerManager().getPlayer(target);

            sender.sendMessage(ChatColor.GRAY + target.getName() + ChatColor.RED + "'s economy:");
        }

        sender.sendMessage(C.CN + "Balance" + C.SS + C.C + "Bucks: " + C.BUCKS + player.getBucks() + "B " + C.C + "Ultra Coins: " + C.UC +
                player.getUltraCoins() + "UC");
    }


}
