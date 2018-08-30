package net.assimilationmc.assicore.command.commands.eco;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.economic.AchievePayDay;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdPay extends AssiCommand {

    private Map<UUID, Map<UUID, Long>> timeouts;

    public CmdPay(AssiPlugin plugin) {
        super(plugin, "pay", "Pay a player some sweet bucks", Lists.newArrayList(), "<player>", "<amount>");
        requirePlayer();
        this.timeouts = Maps.newHashMap();
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        Player sender = (Player) commandSender;
        Player player = UtilPlayer.get(args[0]);

        if (player == null) {
            couldNotFind(sender, args[0]);
            sender.sendMessage(C.C + "Make sure they're on the same server as you.");
            return;
        }

        if (sender == player) {
            sender.sendMessage(prefix(usedLabel) + C.II + "You cannot pay yourself.");
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(prefix(usedLabel) + C.II + "Invalid amount of bucks. Only integers can be paid.");
            return;
        }

        if (!canPay(sender, player)) {
            sender.sendMessage(prefix(usedLabel) + C.II + "To void spam you can only pay the same player 1 time per 30 seconds.");
            return;
        }

        AssiPlayer assiSender = asPlayer(sender);
        if (!assiSender.canAffordBucks(amount)) {
            sender.sendMessage(prefix(usedLabel) + C.II + "You do not have enough bucks to pay this amount.");
            return;
        }

        AssiPlayer target = asPlayer(player);

        assiSender.takeBucks(amount);
        target.addBucks(amount);

        target.sendMessage(prefix(usedLabel) + "You have received " + C.BUCKS + amount + " Bucks" + C.C + " from " + sender.getDisplayName());
        sender.sendMessage(prefix(usedLabel) + "You have sent " + C.BUCKS + amount + " Bucks" + C.C + " to " + target.getDisplayName());

        this.addCooldown(sender, player);

        if (plugin.getAchievementManager().getAchievements(AchievementCategory.ECONOMIC).get("PAY_DAY") == null) return;

        AchievePayDay achievePayDay = (AchievePayDay) plugin.getAchievementManager().getAchievement("PAY_DAY");
        achievePayDay.onPayment(target, amount);

    }

    public boolean canPay(Player payer, Player player) {
        if (!timeouts.containsKey(payer.getUniqueId())) {
            return true;
        } else if (timeouts.get(payer.getUniqueId()).containsKey(player.getUniqueId())
                && UtilTime.elapsed(timeouts.get(payer.getUniqueId()).get(player.getUniqueId()), TimeUnit.SECONDS.toMillis(30))) {
            timeouts.get(payer.getUniqueId()).remove(player.getUniqueId());
            return true;
        }

        return false;
    }

    public void addCooldown(Player payer, Player payee) {
        if (!timeouts.containsKey(payer.getUniqueId())) {
            timeouts.put(payer.getUniqueId(), Maps.newHashMap());
        }
        timeouts.get(payer.getUniqueId()).put(payee.getUniqueId(), UtilTime.now());
    }

}
