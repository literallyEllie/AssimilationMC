package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.BuckRewards;
import net.assimilationmc.assicore.util.C;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdRefer extends AssiCommand {

    public CmdRefer(AssiPlugin plugin) {
        super(plugin, "refer", "Claim a reward by saying who referred you.", Lists.newArrayList(), "<player>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        AssiPlayer player = asPlayer(sender);

        if (!player.getReferredBy().equals("NONE")) {
            player.sendMessage(C.C + "You have already claimed to have been referred by " + C.V + player.getReferredBy() + C.C + ".");
            return;
        }

        if (player.getName().equalsIgnoreCase(args[0])) {
            player.sendMessage(C.C + "You cannot refer yourself! Instead, invite your friends to join and tell them to do " + C.V + "/refer " + player.getName() + C.C +
                    ". You'll get some sweet bucks anyway, even if you're offline!");
            return;
        }

        UUID referrerUuid = plugin.getPlayerManager().getUUID(args[0]);
        if (referrerUuid == null) {
            player.sendMessage(C.C + "We couldn't automatically find the person you mentioned there, but if you say this to a member of staff they can manually " +
                    "apply the funds to the player.");
        } else {
            AssiPlayer referrer = plugin.getPlayerManager().getOfflinePlayer(referrerUuid);

            referrer.addBucks(BuckRewards.REFER_REFERRER);
            player.setReferredBy(referrer.getName());
        }

        if (referrerUuid == null) player.setReferredBy(args[0]);

        player.addBucks(BuckRewards.REFER_REFEREE);
        player.sendMessage(C.C + "Referral complete! Welcome to " + ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "milationMC" + C.C + "!");
    }

}
