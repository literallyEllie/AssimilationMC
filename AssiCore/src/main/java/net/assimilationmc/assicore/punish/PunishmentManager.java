package net.assimilationmc.assicore.punish;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.cmd.CmdConsolePunish;
import net.assimilationmc.assicore.punish.cmd.CmdIPBan;
import net.assimilationmc.assicore.punish.cmd.CmdPunish;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.punish.model.PunishmentData;
import net.assimilationmc.assicore.punish.model.PunishmentOutcome;
import net.assimilationmc.assicore.punish.model.UnpunishData;
import net.assimilationmc.assicore.redis.RedisDatabaseIndex;
import net.assimilationmc.assicore.redis.RedisObjectHolder;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PunishmentManager extends Module implements RedisObjectHolder {

    public static final String PREFIX = C.II + ChatColor.BOLD + "Punish" + C.SS + C.II;
    public static final String REDIS_CHANNEL = "PUNISH";
    private static final String SQL_TABLE = "punishments";
    private Set<UUID> loadQueue;

    private AssiPlayer console;
    private PunishSync punishSync;

    public PunishmentManager(AssiPlugin plugin) {
        super(plugin, "Punishment Manager");
    }

    @Override
    protected void start() {
        loadQueue = Sets.newHashSet();

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + SQL_TABLE + "` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "`punishee` VARCHAR(100) NOT NULL, " +
                    "`punisher` VARCHAR(100) NOT NULL, " +
                    "`punisher_display` VARCHAR(100) NOT NULL, " +
                    "`punish_cat` VARCHAR(100) NOT NULL, " +
                    "`punish_out` VARCHAR(100) NOT NULL, " +
                    "`severity` INT NOT NULL, " +
                    "`punish_issued` BIGINT NOT NULL," +
                    "`ip` VARCHAR(100) NULL," +
                    "`custom_reason` TEXT NULL, " +
                    "`punish_length` BIGINT NOT NULL," +
                    "`unpunisher_display` VARCHAR(100) NULL, " +
                    "`unpunish_time` VARCHAR(100) NULL," +
                    "`unpunish_reason` TEXT NULL, " +
                    "INDEX(punishee)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;").execute();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to make opening statement to SQL!");
            e.printStackTrace();
        }

        punishSync = new PunishSync(this);

        getPlugin().getCommandManager().registerCommand(new CmdPunish(getPlugin()), new CmdIPBan(getPlugin()), new CmdConsolePunish(getPlugin()));

        this.console = new AssiPlayer(getPlugin(), UUID.randomUUID());
        console.setName("CONSOLE");
        console.setDisplayName("CONSOLE");
    }

    @Override
    protected void end() {
        loadQueue.clear();
    }

    /**
     * The method to request a punish profile, upon loading it will attach itself to
     * the player object then do checks.
     *
     * @param assiPlayer the player to get the profile of.
     */
    public void requestPunishProfile(AssiPlayer assiPlayer) {
        if (loadQueue.contains(assiPlayer.getUuid())) return;
        PunishProfile punishProfile;

        loadQueue.add(assiPlayer.getUuid());

        punishProfile = getAllPunishmentsRedis(assiPlayer.getUuid());

        if (punishProfile == null) {
            punishProfile = getAllPunishmentSQL(assiPlayer.getUuid());
        }

        if (punishProfile == null) {
            punishProfile = new PunishProfile(assiPlayer.getUuid());
            punishProfile.setName(assiPlayer.getName());
        }

        assiPlayer.setPunishProfile(punishProfile);
        loadQueue.remove(assiPlayer.getUuid());

        if (assiPlayer.getBase() != null) {

            PunishmentData ban = punishProfile.getEffectiveBan();
            if (ban != null) {
                getPlugin().getServer().getScheduler().runTask(getPlugin(), () ->
                        assiPlayer.getBase().kickPlayer(C.II + (ban.isPerm() ? "Perm" : "Temp") + "-banned" + C.C + "\n\n" +
                                "Category: " + C.V + ban.getPunishmentCategory().getDisplay() + C.C + "\n" +
                                "Reason: " + C.V + ban.getReason() + C.C + "\n\n" +
                                "Expires in: " + C.V + (ban.isPerm() ? "never :(" : UtilTime.formatTimeStamp(ban.getPunishExpiry())) + C.C + "\n\n" +
                                "Disagree? Appeal on our forum: " + C.V + Domain.PROT_FORUM));
                return;
            }

            if (!getPlugin().getServerData().isLobby()) return;

            try (Connection connection = getPlugin().getSqlManager().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT punish_cat, punish_out, custom_reason, punish_issued, punish_length " +
                        "FROM " + SQL_TABLE + " WHERE ip = ? AND unpunisher_display IS NULL");
                preparedStatement.setString(1, assiPlayer.lastIP());
                final ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    long punishIssued = resultSet.getLong("punish_issued");
                    long punishLength = resultSet.getLong("punish_length");

                    PunishmentOutcome punishmentOutcome = PunishmentOutcome.valueOf(resultSet.getString("punish_out"));
                    if (!punishmentOutcome.isPerm() && UtilTime.elapsed(punishIssued, punishLength)) continue;

                    PunishmentCategory punishmentCategory = PunishmentCategory.valueOf(resultSet.getString("punish_cat"));
                    String reason = resultSet.getString("custom_reason") == null ? punishmentCategory.getBaseReason() :
                            resultSet.getString("custom_reason");

                    getPlugin().getServer().getScheduler().runTask(getPlugin(), () ->
                            assiPlayer.getBase().kickPlayer(C.II + (punishmentOutcome.isPerm() ? "Perm" : "Temp") + "-banned" + C.C + "\n\n" +
                                    "Category: " + C.V + punishmentCategory.getDisplay() + C.C + "\n" +
                                    "Reason: " + C.V + reason + C.C + "\n\n" +
                                    "Expires in: " + C.V + (punishmentOutcome.isPerm() ? "never :(" : UtilTime.formatTimeStamp(punishIssued + punishLength)) + C.C + "\n\n" +
                                    "Disagree? Appeal on our forum: " + C.V + Domain.PROT_FORUM));
                    break;
                }

                resultSet.close();
                preparedStatement.close();

            } catch (SQLException e) {
                log(Level.SEVERE, "Failed to IP ban check!");
                e.printStackTrace();
            }

        }

    }

    /**
     * The method that should be used to punish players.
     *
     * @param punisher           The player punishing.
     * @param punishee           The punishee.
     * @param punishmentCategory The category of the punishment.
     * @param reason             the reason for punishing (can be null)
     */
    public void punish(AssiPlayer punisher, AssiPlayer punishee, PunishmentCategory punishmentCategory, String reason) {

        PunishProfile punishProfile = punishee.getPunishProfile();
        if (punishProfile == null) punishProfile = punishee.getPunishmentProfile(false);
        if (punishProfile == null) {
            punisher.sendMessage(C.II + "Error carrying out punishment, couldn't acquire target punishment profile. Try again in about 10 seconds.");
            return;
        }

        punishmentCategory.punish(getPlugin(), punishee, punisher, punishProfile.nextPunishmentSeverity(punishmentCategory), reason);
    }

    /**
     * A method to unpunish a player, and should be used to do so.
     *
     * @param unpunisher   the unpunisher.
     * @param unpunished   the person who is being unpunished.
     * @param punishmentId the punishment id that is being revoked.
     * @param reason       the reason specified they are being unpunished.
     */
    public void unpunish(AssiPlayer unpunisher, AssiPlayer unpunished, int punishmentId, String reason) {

        PunishProfile punishProfile = unpunished.getPunishProfile();
        if (punishProfile == null) punishProfile = unpunished.getPunishmentProfile(false);
        if (punishProfile == null) {
            unpunisher.sendMessage(C.II + "Error carrying out un-punishment, couldn't acquire target punishment profile. Try again in about 10 seconds.");
            return;
        }

        final PunishmentData punishmentData = punishProfile.getPunishmentId(punishmentId);
        if (punishmentData == null) {
            unpunisher.sendMessage(C.II + "Couldn't find that punishment. (id = " + punishmentId + ")");
            return;
        }

        if (punishmentData.isInvalid()) {
            unpunisher.sendMessage(C.II + "This punishment has already been invalidated.");
            return;
        }

        punishProfile.getActivePunishments().get(punishmentData.getPunishmentCategory()).remove(punishmentData);

        UnpunishData unpunishData = new UnpunishData(unpunisher.getDisplayName(), System.currentTimeMillis(), reason);
        punishmentData.setUnpunishData(unpunishData);

        punishProfile.addOldPunishment(true, punishmentData.getPunishmentCategory(), punishmentData);

        punishmentData.getPunishmentCategory().unPunish(getPlugin(), unpunisher, unpunished.getUuid(), unpunished.getName(), punishmentData);
    }

    private PunishProfile getAllPunishmentsRedis(UUID uuid) {
        if (getPlugin().getServerData().isLocal()) return null;

        PunishProfile punishProfile = new PunishProfile(uuid);

        final String redisKey = getPlugin().getPlayerManager().redisKey(uuid);

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);
            if (!jedis.exists(redisKey)) return null;
            punishProfile.setName(jedis.hget(redisKey, "name"));

            List<String> punishmentKeys = jedis.hkeys(redisKey)
                    .stream().filter(s -> s.startsWith(getObjectPrefix()))
                    .collect(Collectors.toList());
            if (punishmentKeys.isEmpty()) {
                // dont believe them!
                return null;

            }

            for (String punishmentKey : punishmentKeys) {

                String serializedData = jedis.hget(redisKey, punishmentKey);
                PunishmentData data = new PunishmentData(serializedData);

                if (data.expired()) {
                    punishProfile.addOldPunishment(false, data.getPunishmentCategory(), data);
                } else punishProfile.addPunishment(data.getPunishmentCategory(), data);

            }

        }

        return punishProfile;

    }

    private PunishProfile getAllPunishmentSQL(UUID uuid) {
        PunishProfile punishUser = new PunishProfile(uuid);

        boolean exists = false;

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + SQL_TABLE + "` WHERE punishee = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                exists = true;

                long punishIssued = resultSet.getLong("punish_issued");
                long punishmentLength = resultSet.getLong("punish_length");

                final UUID punishee = UUID.fromString(resultSet.getString("punishee"));
                final UUID punisher = UUID.fromString(resultSet.getString("punisher"));
                final String punisherDisplay = resultSet.getString("punisher_display");
                final PunishmentCategory punishmentCategory = PunishmentCategory.valueOf(resultSet.getString("punish_cat"));
                final PunishmentOutcome punishmentOutcome = PunishmentOutcome.valueOf(resultSet.getString("punish_out"));
                final int severity = resultSet.getInt("severity");

                final String customReason = resultSet.getString("custom_reason"); // may be null
                final String ip = resultSet.getString("ip");

                UnpunishData unpunishData = null;
                if (resultSet.getString("unpunisher_display") != null) {
                    unpunishData = new UnpunishData(resultSet.getString("unpunisher_display"),
                            resultSet.getLong("unpunish_time"), resultSet.getString("unpunish_reason"));
                }

                final PunishmentData data = new PunishmentData(punishUser.getPunishmentCounter(), punisher, punishee, punisherDisplay, punishmentCategory,
                        punishmentOutcome, severity, punishIssued, punishmentLength, unpunishData);
                data.setCustomReason(customReason);
                data.setIp(ip);

                if (data.expired()) punishUser.addOldPunishment(false, punishmentCategory, data);
                else punishUser.addPunishment(punishmentCategory, data);
            }

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to get punishments to a user from SQL!");
            e.printStackTrace();
        }

        if (exists) {
            punishUser.setName(getPlugin().getPlayerManager().getPlayer(uuid).getName());
        }

        return exists ? punishUser : null;
    }

    public void pushExistPunishment(PunishmentData data) {

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + SQL_TABLE + "` SET " +
                    "punishee = ?, punisher = ?, punisher_display = ?, punish_cat = ?, punish_out = ?, " +
                    "severity = ?, punish_issued = ?, ip = ?, custom_reason = ?," +
                    "punish_length = ?, unpunisher_display = ?, unpunish_time = ?, unpunish_reason = ?" +
                    " WHERE punish_issued = ?");
            preparedStatement.setString(1, data.getPunishee().toString());
            preparedStatement.setString(2, data.getPunisher().toString());
            preparedStatement.setString(3, data.getPunisherDisplay());
            preparedStatement.setString(4, data.getPunishmentCategory().name());
            preparedStatement.setString(5, data.getPunishmentType().name());
            preparedStatement.setInt(6, data.getSeverity());
            preparedStatement.setLong(7, data.getPunishIssued());

            if (data.isIPBan()) preparedStatement.setString(8, data.getIp());
            else preparedStatement.setNull(8, Types.VARCHAR);
            if (data.getCustomReason() != null) preparedStatement.setString(9, data.getCustomReason());
            else
                preparedStatement.setNull(9, Types.VARCHAR);
            preparedStatement.setLong(10, data.getPunishLength());
            if (data.isInvalid()) {
                preparedStatement.setString(11, data.getUnpunishData().getUnpunisherDisplay());
                preparedStatement.setLong(12, data.getUnpunishData().getUnpunishTime());
                preparedStatement.setString(13, data.getUnpunishData().getUnpunishReason());
            } else {
                preparedStatement.setNull(11, Types.VARCHAR);
                preparedStatement.setNull(12, Types.BIGINT);
                preparedStatement.setNull(13, Types.VARCHAR);
            }
            preparedStatement.setLong(14, data.getPunishIssued());

            preparedStatement.execute();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to push punishment request!");
            e.printStackTrace();
        }

    }

    /**
     * Method to push a new punishment to SQL.
     *
     * @param data the punishment request to push.
     */
    public void pushNewPunishmentSQL(PunishmentData data) {
        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + SQL_TABLE + "` " +
                    "(punishee, punisher, punisher_display, punish_cat, punish_out, severity, punish_issued, ip, custom_reason, punish_length)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, data.getPunishee().toString());
            preparedStatement.setString(2, data.getPunisher().toString());
            preparedStatement.setString(3, data.getPunisherDisplay());
            preparedStatement.setString(4, data.getPunishmentCategory().name());
            preparedStatement.setString(5, data.getPunishmentType().name());
            preparedStatement.setInt(6, data.getSeverity());
            preparedStatement.setLong(7, data.getPunishIssued());
            if (data.isIPBan()) preparedStatement.setString(8, data.getIp());
            else preparedStatement.setNull(8, Types.VARCHAR);
            if (data.getCustomReason() != null) preparedStatement.setString(9, data.getCustomReason());
            else preparedStatement.setNull(9, Types.VARCHAR);
            preparedStatement.setLong(10, data.getPunishLength());

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to push new punishment request!");
            e.printStackTrace();
        }
    }

    /**
     * Method to push a new punishment to Redis.
     *
     * @param data The punishment request to push.
     */
    public void pushPunishmentRedis(PunishmentData data) {
        if (getPlugin().getServerData().isLocal()) return;

        String redisKey = getPlugin().getPlayerManager().redisKey(data.getPunishee());
        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);
            if (!jedis.exists(redisKey)) return;
            jedis.hset(redisKey, redisKey(data.getId()), data.toString());
        }

    }

    /**
     * @return the thing to keep punishments in sync between servers.
     */
    public PunishSync getPunishSync() {
        return punishSync;
    }

    @Override
    public String getObjectPrefix() {
        return "punishment_";
    }

    @Override
    public String redisKey(Object object) {
        return getObjectPrefix() + object.toString();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final AsyncPlayerChatEvent e) {
        AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());
        PunishProfile punishProfile = player.getPunishmentProfile(true);
        if (punishProfile == null) return;

        // eff
        PunishmentData mute = punishProfile.getEffectiveMute();
        if (mute != null) {
            e.setCancelled(true);

            player.sendMessage(PunishmentManager.PREFIX + C.II + "Sorry! " + C.C + "You can't talk; you are muted... " + C.II +
                    (mute.isPerm() ? "forever!" : "until " + UtilTime.formatTimeStamp(mute.getPunishExpiry())));

        }

    }

    public AssiPlayer getConsole() {
        return console;
    }

}
