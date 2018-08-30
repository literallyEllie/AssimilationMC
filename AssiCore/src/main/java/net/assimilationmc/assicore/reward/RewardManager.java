package net.assimilationmc.assicore.reward;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.booster.ActiveBooster;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RewardManager extends Module {

    private double bucksBooster, ucBooster;
    private Map<String, Double> customBoosters;

    public RewardManager(AssiPlugin plugin) {
        super(plugin, "Reward Manager");
    }

    @Override
    protected void start() {
        this.bucksBooster = ucBooster = 1;
        this.customBoosters = Maps.newHashMap();
    }

    @Override
    protected void end() {
        customBoosters.clear();
    }

    public void giveBucks(AssiPlayer player, int amount) {
        int newAmount = (int) Math.round(amount * bucksBooster);

        final ActiveBooster activeBooster = getPlugin().getBoosterManager().getActiveBooster();
        if (activeBooster != null) {
            newAmount = activeBooster.getBooster().processBucks(newAmount);
        }

        player.addBucks(newAmount);
    }

    public void giveUltraCoins(AssiPlayer player, int amount) {
        int newAmount = (int) Math.round(amount * ucBooster);

        final ActiveBooster activeBooster = getPlugin().getBoosterManager().getActiveBooster();
        if (activeBooster != null) {
            newAmount = activeBooster.getBooster().processUC(newAmount);
        }

        player.addUltraCoins(newAmount);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerJoinEvent e) {
        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (!player.getLastRewardClaim().containsKey(RewardType.DAILY)
                || UtilTime.elapsed(player.getLastRewardClaim().get(RewardType.DAILY), TimeUnit.DAYS.toMillis(1))) {

            player.setRewardClaimed(RewardType.DAILY);

            String start;

            if (player.getJoins() == 1) {
                start = ChatColor.GOLD + ChatColor.BOLD.toString() + "Welcome to AssimilationMC! ";
            } else start = ChatColor.GOLD + ChatColor.BOLD.toString() + "Happy day! You returned. ";
            player.sendMessage(start + ChatColor.GREEN + "Have this small token " + "of appreciation.");
            RewardType.DAILY.give(player);
        }

        if ((player.getRank().isDonator() || player.getRank().isPromoter()) && (!player.getLastRewardClaim().containsKey(RewardType.DONATE_MONTHLY)
                || UtilTime.elapsed(player.getLastRewardClaim().get(RewardType.DONATE_MONTHLY), TimeUnit.DAYS.toMillis(30)))) {
            player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Wow, is it that time of the month again? " + ChatColor.GREEN + "Have this small " +
                    "gift of appreciation for all the support you have given us " + ChatColor.DARK_RED + ChatColor.BOLD + "❤");

            player.setRewardClaimed(RewardType.DONATE_MONTHLY);
            RewardType.DONATE_MONTHLY.give(player);

        }

    }

    public double getBucksBooster() {
        return bucksBooster;
    }

    public void setBucksBooster(double bucksBooster) {
        this.bucksBooster = bucksBooster;
    }

    public boolean hasBucksBooster() {
        return bucksBooster != 1;
    }

    public double getUCBooster() {
        return ucBooster;
    }

    public void setUCBooster(double ucBooster) {
        this.ucBooster = ucBooster;
    }

    public boolean hasUCBooster() {
        return ucBooster != 1;
    }

    public Map<String, Double> getCustomBoosters() {
        return customBoosters;
    }

    public boolean isBoosterActive(String name) {
        return customBoosters.containsKey(name) && customBoosters.get(name) == 1;
    }

    public void registerBooster(String name, double amount) {
        customBoosters.put(name, amount);
    }

    public void disableBooster(String name) {
        customBoosters.remove(name);
    }

}
