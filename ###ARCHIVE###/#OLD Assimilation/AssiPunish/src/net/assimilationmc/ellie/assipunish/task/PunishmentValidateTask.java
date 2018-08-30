package net.assimilationmc.ellie.assipunish.task;

import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assipunish.punish.PunishmentOffence;
import net.assimilationmc.ellie.assipunish.punish.PunishmentResult;
import net.assimilationmc.ellie.assipunish.punish.data.DataPunishment;
import net.assimilationmc.ellie.assipunish.punish.data.PunishManager;
import net.assimilationmc.ellie.assipunish.punish.data.SQLPunishQuery;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.sql2o.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ellie on 23/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PunishmentValidateTask implements Runnable, Listener {

    private PunishManager punishManager;

    private HashMap<String, DataPunishment> mutes; // ip or uuid
    private HashMap<Object, DataPunishment> bans; // ip or uuid

    public PunishmentValidateTask(PunishManager punishManager){
        this.punishManager = punishManager;
        mutes = new HashMap<>();
        bans = new HashMap<>();
    }

    @Override
    public void run() {
        updateLists();
        mutes.values().removeIf(this::expired);
        bans.values().removeIf(this::expired);
        System.out.println("[Punishments] Expiry task run");
    }


    private void updateLists(){
        mutes.clear();
        bans.clear();
        try(Connection connection = punishManager.getSqlManager().getSql2o().open()){
            List<DataPunishment> punishments = connection.createQuery(SQLPunishQuery.GET_ALL_ACTIVE).executeAndFetch(DataPunishment.class);
            connection.close();
            for (DataPunishment punishment : punishments) {
                PunishmentOffence offence = PunishmentOffence.getPunishInfo(punishment.getType(), punishment.getOffence());
                if(offence != null && offence.getPunishmentResult() == PunishmentResult.MUTE){
                    addMute(punishment);
                } else addBan(punishment);
            }
        }
    }

    public boolean expired(DataPunishment dataPunishment){
        long now = System.currentTimeMillis();
        if(now >= dataPunishment.getExpire()){
            dataPunishment.setUnpunished_time(now);
            dataPunishment.setUnpunished_by("CONSOLE");
            dataPunishment.setUnpunished_reason("Punishment expired");

            PunishmentOffence offence = PunishmentOffence.getPunishInfo(dataPunishment.getType(), dataPunishment.getOffence());
            if(offence != null){
                if(offence.getPunishmentResult() == PunishmentResult.MUTE)
                    mutes.remove(dataPunishment.getUuid() == null ? dataPunishment.getIp() : dataPunishment.getUuid());
                else bans.remove(dataPunishment.getUuid() == null ? dataPunishment.getIp() : dataPunishment.getUuid());
            }
            punishManager.unPunish(dataPunishment, dataPunishment.getUuid() == null ? dataPunishment.getIp() : dataPunishment.getUuid().toString(), true);
            return true;
        }
        return false;
    }

    public void addMute(DataPunishment dataPunishment){
        mutes.put(dataPunishment.getUuid() == null ? dataPunishment.getIp() : dataPunishment.getUuid().toString(), dataPunishment);
    }

    public void addBan(DataPunishment dataPunishment){
        bans.put(dataPunishment.getUuid() == null ? dataPunishment.getIp() : dataPunishment.getUuid().toString(), dataPunishment);
    }

    public void removeMute(String a){
        mutes.remove(a);
    }

    public void removeBan(String a){
        bans.remove(a);
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();

        if(bans.containsKey(uuid.toString()) || bans.containsKey(e.getAddress().getHostAddress())) {

            for (DataPunishment punishment : bans.values()) {
                if((punishment.getUuid() != null && !punishment.getUuid().equals(uuid)) ||
                        (punishment.getIp() != null && !punishment.getIp().equals(e.getAddress().getHostAddress())) || expired(punishment)) continue;

                PunishmentOffence offence = PunishmentOffence.getPunishInfo(punishment.getType(), punishment.getOffence());
                String reason = "&cerror retrieving reason";
                String expiry = "&cerror retrieving expiry";
                if (offence != null) {
                    reason = punishment.getCustomReason() == null ?
                            offence.getReason() + offence.getSuperPunish().getRawReason() : punishment.getCustomReason();
                    expiry = offence.getTime().equals("PERM") ? "Permanent" : Util.getDuration(punishment.getExpire());
                }
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(Util.color("&4&l\nBanned&5&o\nStep back mortal!&4&l\n\n" +
                        " Reason: &9\n" + reason +
                        "&4&l\n\n Expiry: &9\n" + expiry + "&c\n\n" +
                        " Don't agree?&9\n" +
                        " Appeal at &5&owww.assimilationmc.enjin.com&9\nReference ID: &5#" + punishment.getId()));
                break;
            }
        }
    }

    @EventHandler
    public void onTalk(AsyncPlayerChatEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        String ip = e.getPlayer().getAddress().getAddress().getHostAddress();

        if(mutes.containsKey(uuid.toString()) || mutes.containsKey(ip)) {
            e.setCancelled(true);
            for (DataPunishment punishment : mutes.values()) {
                if((punishment.getUuid() != null && !punishment.getUuid().equals(uuid)) ||
                        (punishment.getIp() != null && !punishment.getIp().equals(ip)) || expired(punishment)) continue;

                PunishmentOffence offence = PunishmentOffence.getPunishInfo(punishment.getType(), punishment.getOffence());
                String reason = "&cerror retrieving reason";
                String expiry = "&cerror retrieving expiry";
                if (offence != null) {
                    reason = punishment.getCustomReason() == null ?
                            offence.getReason() + offence.getSuperPunish().getRawReason() : punishment.getCustomReason();
                    expiry = offence.getTime().equals("PERM") ? "Permanent" : Util.getDuration(punishment.getExpire());
                }
                e.getPlayer().sendMessage(Util.color("&a&lMUTE &cNo talking! You have been muted for &9"+reason+" &cuntil "+expiry+"&c!"));
            }
        }


    }

}
