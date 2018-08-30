package net.assimilationmc.ellie.assipunish.punish.data;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.manager.IManager;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.manager.SQLManager;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assipunish.AssiPunish;
import net.assimilationmc.ellie.assipunish.punish.Punishment;
import net.assimilationmc.ellie.assipunish.punish.PunishmentOffence;
import net.assimilationmc.ellie.assipunish.punish.PunishmentResult;
import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.util.Channels;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.sql2o.Connection;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PunishManager implements IManager {

    private SQLManager sqlManager;

    @Override
    public boolean load() {

        sqlManager = AssiCore.getCore().getModuleManager().getSQLManager();

        try(Connection connection = sqlManager.getSql2o().open()){
            connection.createQuery(SQLPunishQuery.INITIAL_STATEMENT).executeUpdate().close();
        }

        return true;
    }

    @Override
    public boolean unload() {
        sqlManager = null;
        return true;
    }

    @Override
    public String getModuleID() {
        return "punish";
    }

    public List<DataPunishment> getPunishments(String object){
        try(Connection connection = sqlManager.getSql2o().open()){
            return connection.createQuery(SQLPunishQuery.GET_PLAYER_PUNISHMENTS).addParameter("uuid", object).executeAndFetch(DataPunishment.class);
        }
    }

    public List<DataPunishment> getPunishmentsOf(String object){
        try(Connection connection = sqlManager.getSql2o().open()){
            return connection.createQuery(SQLPunishQuery.GET_PLAYER_PUNISHMENTS).addParameter("uuid", object).executeAndFetch(DataPunishment.class);
        }
    }

    public List<DataPunishment> getPunishmentsOfType(String object, Punishment type){
        try(Connection connection = sqlManager.getSql2o().open()){
            return connection.createQuery(SQLPunishQuery.GET_PUNISHMENT_OF_TYPE).addParameter("uuid", object)
                    .addParameter("type", type.toString()).executeAndFetch(DataPunishment.class);
        }
    }

    public DataPunishment getLatestPunishmentOfType(String object, Punishment type){
        try(Connection connection = sqlManager.getSql2o().open()){
            List<DataPunishment> dataPunishment = connection.createQuery(SQLPunishQuery.GET_LATEST_PUNISHMENT_OF_TYPE).
                    addParameter("uuid", object).addParameter("type", type.toString()).executeAndFetch(DataPunishment.class);
            connection.close();
            if(!dataPunishment.isEmpty()){
                return dataPunishment.get(0);
            }
        }
        return null;
    }

    public boolean hasActivePunishment(String object, Punishment punishment){
        try(Connection connection = sqlManager.getSql2o().open()){
            List<DataPunishment> dataPunishment = connection.createQuery(SQLPunishQuery.GET_LATEST_PUNISHMENT_OF_TYPE).
                    addParameter("uuid", object).addParameter("type", punishment.toString()).executeAndFetch(DataPunishment.class);
            connection.close();
            if(!dataPunishment.isEmpty()){
                DataPunishment dataPunishment1 = dataPunishment.get(0);
                return dataPunishment1.getExpire() > System.currentTimeMillis() && dataPunishment1.getUnpunishedBy() == null;
            }
        }
        return false;
    }

    public List<DataPunishment> getActivePunishments(String object){
        try(Connection connection = sqlManager.getSql2o().open()){
            List<DataPunishment> dataPunishments = connection.createQuery(SQLPunishQuery.GET_ACTIVE).addParameter("uuid", object)
                    .executeAndFetch(DataPunishment.class);
            connection.close();
            return dataPunishments;
        }
    }

    public void givePunishment(String punisher, String object, Punishment punishment, String customReason){
        PunishmentOffence offence = calculateNextOffence(object, punishment);

        DataPunishment dataPunishment = new DataPunishment(object, punishment, offence.getOffence(), System.currentTimeMillis(),
                (offence.getTime().equals("-1") || offence.getTime().equals("PERM")
                ? -1 : Util.parseDuration(offence.getTime())), punisher);
        if(customReason != null){
            dataPunishment.setCustom_reason(customReason);
        }

        try(Connection connection = sqlManager.getSql2o().open()){
            connection.createQuery(SQLPunishQuery.INSERT_PUNISHMENT).
                    addParameter("uuid", object).addParameter("type", punishment.toString())
                    .addParameter("offence", dataPunishment.getOffence()).addParameter("issued", dataPunishment.getIssued())
                    .addParameter("expire", dataPunishment.getExpire())
                    .addParameter("punisher", punisher).addParameter("customReason", customReason).executeUpdate().close();

            if(offence.getPunishmentResult() == PunishmentResult.MUTE) {
                AssiPunish.getAssiPunish().getPunishmentValidateTask().addMute(dataPunishment);
            }else if(offence.getPunishmentResult() != PunishmentResult.BLACKLIST){
                AssiPunish.getAssiPunish().getPunishmentValidateTask().addBan(dataPunishment);
            }
            carryOutPunishment(punisher, object, offence, customReason);
        }
    }

    private PunishmentOffence calculateNextOffence(String object, Punishment type){
        DataPunishment a = getLatestPunishmentOfType(object, type);
        if(a == null){
            return PunishmentOffence.getPunishInfo(type, 0);
        }

        if(PunishmentOffence.getNextPunishment(type, a.getOffence()) == null){
            return PunishmentOffence.getMaxPunishment(type);
        }

        long given = a.getIssued();
        long expire = a.getExpire();
        // if the player is on before the expiry is reached
        if(expire != -1 && System.currentTimeMillis() < expire && a.getUnpunishedBy() == null){
            return PunishmentOffence.getPunishInfo(type, a.getOffence());
        }

        long diff = System.currentTimeMillis() - given;
        if(diff > Util.parseDuration("3months")){
            return PunishmentOffence.getPunishInfo(type, 0);
        }
        return PunishmentOffence.getNextPunishment(type, a.getOffence());
    }

    public void unPunish(DataPunishment dataPunishment, String object, boolean auto){

        try(Connection connection = sqlManager.getSql2o().open()){
            connection.createQuery(SQLPunishQuery.UNPUNISH).addParameter("punishedBy", dataPunishment.getUnpunishedBy())
                    .addParameter("unpunishedtime", dataPunishment.getUnpunishedTime())
                    .addParameter("unpunishReason", dataPunishment.getUnpunishedReason()).addParameter("id", dataPunishment.getId()).executeUpdate().close();
            if(!auto){
                if(isMute(dataPunishment.getType(), dataPunishment.getOffence()))
                    AssiPunish.getAssiPunish().getPunishmentValidateTask().removeMute(dataPunishment.getUuid() == null
                            ? dataPunishment.getIp() : dataPunishment.getUuid().toString());
                else AssiPunish.getAssiPunish().getPunishmentValidateTask().removeBan(dataPunishment.getUuid() == null
                        ? dataPunishment.getIp() : dataPunishment.getUuid().toString());
            }
        }

        if(!auto) {
            String verb = "unknown";
            PunishmentOffence offence = PunishmentOffence.getPunishInfo(dataPunishment.getType(), dataPunishment.getOffence());
            if (offence != null) {
                switch (offence.getPunishmentResult()) {
                    case BLACKLIST:
                        verb = "unblacklisted";
                        break;
                    case BAN:
                        verb = "unbanned";
                        break;
                    case BAN_ALL:
                        verb = "unbanned all";
                        break;
                    case IP_BAN:
                        verb = "unbanned ip";
                        break;
                    case MUTE:
                        verb = "unmuted";
                        break;
                    case BAN_AND_IP:
                        verb = "unbanned and unip banned";
                        break;
                }
            }

            Player t = Bukkit.getPlayer(object);
            if(t == null){
                try{
                    t = Bukkit.getPlayer(UUID.fromString(object));
                    if(t != null){
                        t.sendMessage(Util.color("&c&lYour punishment has been expired for &f"+dataPunishment.getUnpunishedReason()+" &c&l. You may now speak again."));
                    }
                    ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(dataPunishment.getUnpunishedBy(),
                            Bukkit.getOfflinePlayer(UUID.fromString(object)).getName(), verb, dataPunishment.getUnpunishedReason());
                    AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage(
                            "**" + dataPunishment.getUnpunishedBy() + "** issued a `"+verb+"` to **" +
                                    Bukkit.getOfflinePlayer(UUID.fromString(object)).getName()+ "** for `" + dataPunishment.getUnpunishedReason() + "`").queue();
                    return;
                }catch(IllegalArgumentException ignored){
                }
            }else t.sendMessage(Util.color("&c&lYour punishment has been expired for &f"+dataPunishment.getUnpunishedReason()+" &c&l. You may now speak again."));

            ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(dataPunishment.getUnpunishedBy(),
                    object, verb, dataPunishment.getUnpunishedReason());
            AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage(
                    "**" + dataPunishment.getUnpunishedBy() + "** issued a `"+verb+"` to **" +
                            object+ "** for `" + dataPunishment.getUnpunishedReason() + "`").queue();

        }

    }

    private void carryOutPunishment(String punisher, String object, PunishmentOffence punishment, String customReason) {
        OfflinePlayer player = null;
        boolean ip = false;
        try{
            UUID uuid = UUID.fromString(object);
            player = Bukkit.getOfflinePlayer(uuid);
        }catch(IllegalArgumentException e){
            ip = true;
        }

        String id = ip ? object : player.getName();

        String reason = customReason == null ? punishment.getReason() + punishment.getSuperPunish().getRawReason() : customReason;

        switch (punishment.getPunishmentResult()) {
            case WARN:
                if (!ip && player.isOnline()) {
                    Bukkit.getPlayer(player.getUniqueId()).sendMessage(Util.color("&c&lWARNING &3You have been warned for &9" +
                            reason + "&3."));
                }else if(ip){
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(object))
                            assiPlayer.sendMessage("&c&lWARNING &3You have been warned for &9" +
                                    reason + "&3.");
                    });
                }
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "warned", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `warning` to **" + id + "** for `" + reason + "`").queue();
                break;
            case KICK:
                if (!ip && player.isOnline()) {
                    Util.kickPlayer(Bukkit.getPlayer(player.getUniqueId()), "&c&lKicked&c&l\n\nReason: &9\n"+reason+"&9\n\nYou may reconnect immediately");
                }else if(ip){
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(object))
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()),
                                    "&c&lKicked\n\nReason: &9\n"+reason+"&9\n\nYou may reconnect immediately");
                    });
                }
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "kicked", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `kicked` to **" + id + "** for `" + reason + "`").queue();
                break;
            case MUTE:
                if (!ip && player.isOnline()) {
                    Bukkit.getPlayer(player.getUniqueId()).sendMessage(Util.color("&c&lMUTE &3You have been muted for &9" +
                            reason + "&3 For &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime())));
                }else if(ip){
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(object))
                            assiPlayer.sendMessage("&c&lMUTE &3You have been muted for &9" +
                                    reason + "&3 For &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                    });
                }
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "muted", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `mute` to **" + id + "** for `" + reason + "`").queue();
                break;
            case BAN:
                if (!ip && player.isOnline()) {
                    Util.kickPlayer(Bukkit.getPlayer(player.getUniqueId()), "&c&lBanned\n\nReason: &9\n" + reason +
                            "&c&l\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                }else if(ip){
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(object))
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lBanned\n\nReason: &9\n" + reason +
                                    "\n\n&c&lExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                    });
                }
                // BAN
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "banned", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `ban` to **" + id + "** for `" + reason + "`").queue();
                break;
            case BAN_ALL:
                if(!ip && player.isOnline()){
                    String pIp = Bukkit.getPlayer(player.getUniqueId()).getAddress().getAddress().getHostAddress();
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(pIp)) {
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lBanned\n\nReason: &9\n" + reason +
                                    "&c&l\n\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                        }
                    });
                }
                else if(ip) {
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(id)) {
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lBanned\n\nReason: &9\n" + reason +
                                    "&c&l\n\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                        }
                    });
                }
                // BAN ALL IPS ON ACCOUNT
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "banned all", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `ban-all-accounts` to **" + id + "** for `" + reason + "`").queue();
                break;
            case IP_BAN:
                if(!ip && player.isOnline()){
                    String pIp = Bukkit.getPlayer(player.getUniqueId()).getAddress().getAddress().getHostAddress();
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(pIp)) {
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lIP-Banned\n\nReason: &9\n" + reason +
                                    "&c&l\n\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                        }
                    });
                }
                else if(ip) {
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(id)) {
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lIP-Banned\n\nReason: &9\n" + reason +
                                    "&c&l\n\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                        }
                    });
                }
                // BAN ALL IPS ON ACCOUNT
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "ip-banned", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `ip-ban` to **" +id + "** for `" + reason + "`").queue();
                break;
            case BAN_AND_IP:
                if(!ip && player.isOnline()){
                    String pIp = Bukkit.getPlayer(player.getUniqueId()).getAddress().getAddress().getHostAddress();
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(pIp)) {
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lAll accounts banned and IP-Banned\n\nReason: &9\n" + reason +
                                    "&c&l\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                        }
                    });
                }
                else if(ip) {
                    ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                        if (assiPlayer.getIP().equals(id)) {
                            Util.kickPlayer(Bukkit.getPlayer(assiPlayer.getUuid()), "&c&lAll accounts banned and IP-Banned\n\nReason: &9\n" + reason +
                                    "&c&l\nExpiry: &9" + (punishment.getTime().equals("PERM") ? "Permanent" : punishment.getTime()));
                        }
                    });
                }
                // BAN ALL IPS ON ACCOUNT
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "ban-all", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `ban-all` to **" + id + "** for `" + reason + "`").queue();
                break;
            case BLACKLIST:
                ModuleManager.getModuleManager().getStaffChatManager().punishmentUpdateMessage(punisher, id, "blacklist from UHC", reason);
                AssiDiscord.getAssiDiscord().getDiscord().getTextChannelById(String.valueOf(Channels.BOT_LOGS)).sendMessage("**" + punisher + "**" +
                        " issued a `blacklist from UHC` to **" + id + "** for `" + reason + "`").queue();

                //do some uhc checks
                break;
        }
    }

    public boolean isMute(Punishment punishment, int offence){
        PunishmentOffence a = PunishmentOffence.getPunishInfo(punishment, offence);
        if(a != null) {
            PunishmentResult result = a.getPunishmentResult();
            return result == PunishmentResult.MUTE;
        }
        return false;
    }

    private OfflinePlayer getPlayer(UUID uuid){
        return Bukkit.getOfflinePlayer(uuid);
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }
}
