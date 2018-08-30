package net.assimilationmc.assicore.donate;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DonationManager extends Module implements RedisChannelSubscriber {

    public DonationManager(AssiPlugin plugin) {
        super(plugin, "Donation Manager");
    }

    @Override
    protected void start() {
        getPlugin().getRedisManager().registerChannelSubscriber("DONATE", this);

    }

    @Override
    protected void end() {

    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        final String[] args = message.getArgs();
        Player player = UtilPlayer.get(UUID.fromString(args[0]));
        if (player == null) return;

        AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);

        DonationPackage donationPackage = DonationPackage.valueOf(args[1]);

        if (message.getSubject().equals(DonationUpdateType.INITIAL.name())) {

            UtilServer.broadcast("");
            UtilServer.broadcast(C.SS + C.V + assiPlayer.getName() + C.II + " just donated for the " + C.V + donationPackage.getName() + C.II + "." +
                    "Thanks a lot! You can get awesome perks as well by donating at " + C.V + Domain.PROT_STORE + C.II + " and also help us to keep " +
                    "the network running at the same time!");
            UtilServer.broadcast("");

            switch (donationPackage) {
                case RANK_INFERNAL:
                case RANK_INFERNAL_LIFE:
                    if (!assiPlayer.getRank().isHigherThanOrEqualTo(Rank.INFERNAL)) {
                        assiPlayer.setRank(Rank.INFERNAL);
                    }

                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 10, 1);
                    assiPlayer.sendMessage("");
                    assiPlayer.sendMessage(C.SS + C.II + "Thank you for purchasing the " + Rank.INFERNAL.getPrefix() + C.II + " rank! " +
                            "We hope you will enjoy your new perks in return for your support to us.");
                    assiPlayer.sendMessage("");
                    break;
                case RANK_DEMONIC:
                case RANK_DEMONIC_LIFE:
                    if (!assiPlayer.getRank().isHigherThanOrEqualTo(Rank.DEMONIC)) {
                        assiPlayer.setRank(Rank.DEMONIC);
                    }

                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 10, 1);
                    assiPlayer.sendMessage("");
                    assiPlayer.sendMessage(C.SS + C.II + "Thank you so much for purchasing the " + Rank.DEMONIC.getPrefix() + C.II + " rank! " +
                            "We hope you will enjoy your new perks in return for your support to us.");
                    assiPlayer.sendMessage("");
                    break;

                case BUCKS_2_15:
                    assiPlayer.addBooster("15_DOUBLE_BUCKS");
                case BUCKS_2_30:
                    if (donationPackage == DonationPackage.BUCKS_2_30)
                        assiPlayer.addBooster("30_DOUBLE_BUCKS");
                case UC_2_15:
                    if (donationPackage == DonationPackage.UC_2_15)
                        assiPlayer.addBooster("15_DOUBLE_UC");
                case UC_2_30:
                    if (donationPackage == DonationPackage.UC_2_30)
                        assiPlayer.addBooster("30_DOUBLE_UC");

                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 10, 1);
                    assiPlayer.sendMessage("");
                    assiPlayer.sendMessage(C.SS + C.II + "Thank you so much for purchasing the " + C.V + donationPackage.getName() + C.II + " booster! " +
                            "You will soon be a friend of everyone's!");
                    assiPlayer.sendMessage(ChatColor.GOLD + "Your booster will show in " + C.V + "/boosters" + ChatColor.RED + " But if it is an XP one, you can only use that on" +
                            " UHC game servers.");
                    assiPlayer.sendMessage("");

                case POCKET_ULTRA_COINS:
                    assiPlayer.addUltraCoins(50);
                case POUCH_ULTRA_COINS:
                    if (donationPackage == DonationPackage.POUCH_ULTRA_COINS)
                        assiPlayer.addUltraCoins(150);
                case BOX_ULTRA_COINS:
                    if (donationPackage == DonationPackage.BOX_ULTRA_COINS)
                        assiPlayer.takeUltraCoins(500);
                case VAULT_ULTRA_COINS:
                    if (donationPackage == DonationPackage.VAULT_ULTRA_COINS)
                        assiPlayer.addUltraCoins(1000);

                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 10, 1);
                    assiPlayer.sendMessage("");
                    assiPlayer.sendMessage(C.SS + C.II + "Thank you so much for purchasing the " + C.V + donationPackage.getName() + C.II + " currency pack! " +
                            "Don't spend it all at once!");
                    assiPlayer.sendMessage("");

                default:
                    assiPlayer.sendMessage("");
                    assiPlayer.sendMessage(C.SS + C.II + "Hey! Just got word you donated for " + C.V + donationPackage.getName() + C.II +
                            ". I don't know what that is. Please report to a member of staff so you can get your donation items!");
                    assiPlayer.sendMessage("");
            }
        }

        if (message.getSubject().equals(DonationUpdateType.EXPIRY.name())) {

            assiPlayer.sendMessage("");
            assiPlayer.sendMessage(C.SS + C.II + "Oh no! It looks like your " + C.V + donationPackage.getName() + C.II + " subscription has expired! " +
                    "Fear not! You can renew it here " + C.V + Domain.PROT_STORE + C.II + " Though for now, we will have to remove it...");
            assiPlayer.sendMessage("");

            switch (donationPackage) {
                case RANK_DEMONIC:
                case RANK_INFERNAL:
                    assiPlayer.setRank(Rank.PLAYER);
                    break;
            }

        }

        if (message.getSubject().equals(DonationUpdateType.REFUND.name())) {

            assiPlayer.sendMessage("");
            assiPlayer.sendMessage(C.SS + C.II + "Looks like you were recently refunded. No worries! However I will have to take back those gifts I gave " +
                    "to you before. Its the thought that counts!");
            assiPlayer.sendMessage("");

            switch (donationPackage) {
                case RANK_DEMONIC:
                    assiPlayer.setRank(Rank.PLAYER);
                    break;
                case RANK_INFERNAL:
                    assiPlayer.setRank(Rank.PLAYER);
                    break;
                case POCKET_ULTRA_COINS:
                    assiPlayer.takeUltraCoins(50);
                    break;
                case POUCH_ULTRA_COINS:
                    assiPlayer.takeUltraCoins(150);
                    break;
                case BOX_ULTRA_COINS:
                    assiPlayer.takeUltraCoins(500);
                    break;

            }

        }

        if (message.getSubject().equals(DonationUpdateType.CHARGEBACK.name())) {
            AssiPlayer hypo = new AssiPlayer(getPlugin(), UUID.randomUUID());
            hypo.setName("CONSOLE");
            hypo.setDisplayName("CONSOLE");
            getPlugin().getPunishmentManager().punish(hypo, assiPlayer, PunishmentCategory.CHARGE_BACK, null);
        }

        if (message.getSubject().equals(DonationUpdateType.RENEWAL.name())) {
            assiPlayer.sendMessage("");
            assiPlayer.sendMessage(C.SS + C.II + "Hello its me again! Happy to see you, thank you so much for renewing your subscription with us, it really means a lot" +
                    "Enjoy your renewal bonuses!");
            assiPlayer.sendMessage("");

            switch (donationPackage) {
                case RANK_DEMONIC:

                    break;
                case RANK_INFERNAL:

                    break;
            }

        }

    }

}
