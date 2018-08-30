package net.assimilationmc.assibungee.donate;

import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.buycraft.plugin.bungeecord.event.DonationUpdateEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class DonationHandler extends Module {

    //    private File donateInfo;
//    private DonateInfoFile donateInfoFile;
//
    public DonationHandler(AssiBungee assiBungee) {
        super(assiBungee, "Donation Manager");
    }

    @Override
    protected void start() {
//        this.donateInfo = new File(getPlugin().getDataFolder(), "donate_info.json");
//        if (!donateInfo.exists()) {
//            donateInfoFile = new DonateInfoFile();
//            donateInfoFile.setLastGiveaway(0);
//            save();
//        } else {
//
//            try {
//                JsonReader reader = new JsonReader(new FileReader(donateInfo));
//                donateInfoFile = getPlugin().getPlayerManager().getGson().fromJson(reader, new TypeToken<DonateInfoFile>() {
//                }.getType());
//            } catch (IOException e) {
//                log(Level.WARNING, "Failed to read donate info file.");
//                e.printStackTrace();
//            }
//        }
//
//        int start = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//
//        if (start == 1 && (donateInfoFile.getLastGiveaway() == 0 ||
//                UtilTime.elapsed(donateInfoFile.getLastGiveaway(), TimeUnit.DAYS.toMillis(1)))) {
//            issueBoosters(true);
//            return;
//        }
//
//        if (start > 28) {
//
//            getPlugin().getProxy().getScheduler().schedule(getPlugin(), () -> {
//                int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//
//                if (today == 1) {
//                    issueBoosters(true);
//                }
//
//            }, 1, 24, TimeUnit.HOURS);
//        }
//
    }

    @Override
    protected void end() {

    }

//    public void issueBoosters(boolean monthly) {
//        if (monthly) {
//            donateInfoFile.setLastGiveaway(System.currentTimeMillis());
//        }
//
//        getPlugin().getRedisManager().sendPubSubMessage("DONATE", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
//                getPlugin().getServerData().getId(), "BOOSTERS", new String[]{"memes"}));
//    }

    @EventHandler
    public void on(DonationUpdateEvent e) {
        final UUID playerUuid = e.getPlayerUuid();
        // should be called only when player is online.
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUuid);
        if (player == null) return;

        DonationPackage donationPackage = DonationPackage.fromId(e.getPackageId());
        if (donationPackage == null) {
            throw new IllegalArgumentException("Package ID " + e.getPackageId() + " does not link to any donation package!");
        }

        getPlugin().getRedisManager().sendPubSubMessage("DONATE", new RedisPubSubMessage(player.getServer().getInfo().getName(),
                getPlugin().getServerData().getId(), e.getUpdateType().name(), new String[]{player.getUniqueId().toString(), donationPackage.name()}));
    }

//    private void save() {
//        try {
//            FileWriter writer = new FileWriter(donateInfo);
//            writer.write(getPlugin().getPlayerManager().getGson().toJson(donateInfoFile));
//            writer.close();
//
//        } catch (IOException e) {
//            log(Level.SEVERE, "Failed to save donate info file!");
//            e.printStackTrace();
//        }
//    }

}
