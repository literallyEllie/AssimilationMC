package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdHeal extends AssiCommand {

    public CmdHeal() {
        super("heal", PermissionLib.CMD.HEAL, "heal [player]", "Heal everything");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player targetPlayer = null;

        if (args.length == 1 && sender.hasPermission(PermissionLib.CMD.HEAL_OTHER)) {

            targetPlayer = Bukkit.getPlayer(args[0]);

            if (targetPlayer == null) {
                sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }

            if (targetPlayer.isDead()) {
                sendPMessage(sender, "Player is already dead. You were too late.");
                return;
            }

        } else if (args.length == 0 && sender instanceof Player) {
        } else {
            sendMessage(sender, correctUsage());
            return;
        }

        if (targetPlayer != null) {
            targetPlayer.setFoodLevel(20);
            targetPlayer.setHealthScale(20);
            targetPlayer.setFireTicks(0);

            for (PotionEffect effect : targetPlayer.getActivePotionEffects()) {
                targetPlayer.removePotionEffect(effect.getType());
            }

            sendPMessage(sender, "Healed " + ColorChart.VARIABLE + targetPlayer.getName() + ColorChart.R + ".");
        } else {
            ((Player) sender).setFoodLevel(20);
            ((Player) sender).setHealthScale(20);
            ((Player) sender).setHealth(20);
            ((Player) sender).setFireTicks(0);
            for (PotionEffect effect : ((Player) sender).getActivePotionEffects()) {
                ((Player) sender).removePotionEffect(effect.getType());
            }

            sendPMessage(sender, "Healed " + ColorChart.VARIABLE + sender.getName() + ColorChart.R + ".");
        }
    }

}
