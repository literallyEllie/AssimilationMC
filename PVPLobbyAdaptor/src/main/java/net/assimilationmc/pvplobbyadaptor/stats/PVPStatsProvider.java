package net.assimilationmc.pvplobbyadaptor.stats;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PVPStatsProvider extends Module {

    private final String TABLE = "stats_warmup";
    private Map<UUID, PVPPlayer> playerMap;

    public PVPStatsProvider(AssiPlugin plugin) {
        super(plugin, "PVP Stats Provider");
    }

    @Override
    protected void start() {
        playerMap = Maps.newHashMap();

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (" +
                    "`uuid` VARCHAR(100) NOT NULL UNIQUE, " +
                    "`name` VARCHAR(100) NOT NULL, " +
                    "`level` INT(100)," +
                    "`xp` INT(100)," +
                    "`kills` INT(100)," +
                    "`deaths` INT(100)," +
                    "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to create table on startup.");
            e.printStackTrace();
            getPlugin().getDiscordCommunicator().messageChannel("ADMIN", "[SQL] Fatal error starting up PVP Stats Provider on " +
                    getPlugin().getServerData().getId());
        }

    }

    @Override
    protected void end() {
        playerMap.values().forEach(this::pushPlayer);
        playerMap.clear();
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();

        if (playerMap.containsKey(player.getUniqueId())) {
            pushPlayer(playerMap.get(player.getUniqueId()));
            playerMap.remove(player.getUniqueId());
        }

    }

    /**
     * Get a player's lobby PVP stats.
     *
     * @param player the player to get.
     * @return the loaded pvp player, if it failed to load it is null.
     */
    public PVPPlayer getPlayer(Player player) {
        if (playerMap.containsKey(player.getUniqueId())) return playerMap.get(player.getUniqueId());
        PVPPlayer pvpPlayer = new PVPPlayer(player.getUniqueId());
        pvpPlayer.setName(player.getName());

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE uuid = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                pvpPlayer.setLevel(resultSet.getInt("level"));
                pvpPlayer.setXp(resultSet.getInt("xp"));
                pvpPlayer.setKills(resultSet.getInt("kills"));
                pvpPlayer.setDeaths(resultSet.getInt("deaths"));
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to get PVP player " + player.getName());
            e.printStackTrace();
            return null;
        }

        playerMap.put(player.getUniqueId(), pvpPlayer);

        return pvpPlayer;
    }

    /**
     * Push a player's data to the SQL.
     *
     * @param player the player to put into the database.
     */
    public void pushPlayer(PVPPlayer player) {

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE + " (uuid, name, level, xp, kills, deaths) " +
                    "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), level = VALUES(level), xp = VALUES(xp), " +
                    "kills = VALUES(kills), deaths = VALUES(deaths)");
            preparedStatement.setString(1, player.getUuid().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setInt(3, player.getLevel());
            preparedStatement.setInt(4, player.getXp());
            preparedStatement.setInt(5, player.getKills());
            preparedStatement.setInt(6, player.getDeaths());
            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to push the PVP stats of " + player.getName());
            e.printStackTrace();
        }

    }

}
