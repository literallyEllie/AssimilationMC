package net.assimilationmc.assicore.auth;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class AuthManager extends Module {

    private Map<UUID, AuthUser> authUserMap;
    private Map<UUID, Integer> authExpireTasks;
    private RFC6238 rfc6238;

    public AuthManager(AssiPlugin plugin) {
        super(plugin, "Authentication Manager");
    }

    @Override
    protected void start() {
        this.authUserMap = Maps.newHashMap();
        this.authExpireTasks = Maps.newHashMap();
        this.rfc6238 = new RFC6238(getPlugin());

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `auth` (" +
                    "`uuid` VARCHAR(100) NOT NULL PRIMARY KEY UNIQUE, " +
                    "`secret` TINYTEXT NULL, " +
                    "`last_ip` VARCHAR(100) NULL, " +
                    "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8;").execute();
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to make open connection to create auth table.");
            e.printStackTrace();
        }

    }

    @Override
    protected void end() {
        authUserMap.clear();
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (player.getRank().isHigherThanOrEqualTo(Rank.ADMIN) && !player.isVerified()) {
            initPlayer(player);
        }

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (!authUserMap.containsKey(player.getUniqueId())) return;

        // mod log or something idk
        getPlugin().getDiscordCommunicator().messageChannel("BOT_LOGS",
                player.getName() + " failed to verify themselves on " + getPlugin().getServerData().getId() + " [" + player.getAddress()
                        .getAddress().getHostAddress() + "]");


        if (authExpireTasks.containsKey(player.getUniqueId())) {
            getPlugin().getPunishmentManager().punish(getPlugin().getPunishmentManager().getConsole(), getPlugin().getPlayerManager().getPlayer(player),
                    PunishmentCategory.MALICIOUS_ACTIONS, "[FBI] Failure to comply with authentication.");
            Bukkit.getScheduler().cancelTask(authExpireTasks.get(player.getUniqueId()));
            authExpireTasks.remove(player.getUniqueId());
        }

        rfc6238.playerQuit(player);
    }

    @EventHandler
    public void on(final PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!authUserMap.containsKey(player.getUniqueId())) return;

        Location to = e.getTo();
        Location fra = e.getFrom();

        if (fra.getBlockX() != to.getBlockX() || fra.getBlockZ() != to.getBlockZ())
            e.setCancelled(true);
    }

    @EventHandler
    public void on(final AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (!authUserMap.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);

        AssiPlayer pl = getPlugin().getPlayerManager().getPlayer(player.getUniqueId());

        AuthUser authUser = authUserMap.get(player.getUniqueId());
        authUser.setVerified(rfc6238.authenticate(pl, authUser, e.getMessage().trim()));
        if (!authUser.isVerified()) {
            player.sendMessage(C.II + "Incorrect code.");
            remind(player);
            return;
        } else setSecret(authUser);

        if (authExpireTasks.containsKey(pl.getUuid())) {
            Bukkit.getScheduler().cancelTask(authExpireTasks.get(pl.getUuid()));
            authExpireTasks.remove(pl.getUuid());
        }

        pl.sendMessage(C.C + "Account verified.");
        pl.setVerified(true);
        authUserMap.remove(pl.getUuid());
    }

    @EventHandler
    public void on(final PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        if (authUserMap.containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            remind(player);
        }
    }

    private void initPlayer(AssiPlayer player) {
        AuthUser authUser = getUser(player.getUuid());

        boolean firstTime = authUser == null;
        if (firstTime)
            authUser = createUser(player);

        if (authUserMap.containsKey(authUser.getUuid())) {
            if (!authUser.getLastIp().equals(player.lastIP())) {
                player.sendMessage(C.II + "Your IP differs from your last login so you will be required to re-authenticate.");
                player.setVerified(false);
                authUserMap.remove(authUser.getUuid());
            } else {
                player.sendMessage(C.C + "Account verified.");
                authUserMap.remove(player.getUuid());
                player.setVerified(true);
                return;
            }
        }
        authUser.setLastIp(player.lastIP());
        authUserMap.put(player.getUuid(), authUser);

        player.sendMessage(C.II + ChatColor.BOLD + "This account requires authentication.");

        if (firstTime) {
            player.sendMessage(C.C + "It looks like this first time, please follow the following instructions.");
            rfc6238.firstTimePlayer(player, authUser);
            player.sendMessage(C.II + "Please open your Google Authenticator app and scan the code.");
            return;
        }

        player.sendMessage(C.II + "Please open your Google Authenticator App and post the code in chat.");
        player.sendMessage(C.C + ChatColor.ITALIC + "Failure to comply with result in a ban.");

        authExpireTasks.put(player.getUuid(), Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {

            getPlugin().getPunishmentManager().punish(getPlugin().getPunishmentManager().getConsole(), player,
                    PunishmentCategory.MALICIOUS_ACTIONS, "[FBI] Failure to comply with authentication.");
            // do something
            authExpireTasks.remove(player.getUuid());

        }, 20 * 120));

    }

    private AuthUser getUser(UUID uuid) {
        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `auth` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());

            final ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return null;

            AuthUser authUser = new AuthUser(uuid);
            authUser.setSecret(resultSet.getString("secret"));
            authUser.setLastIp(resultSet.getString("last_ip"));

            resultSet.close();
            preparedStatement.close();

            return authUser;
        } catch (SQLException e) {
            getPlugin().getLogger().warning("Failed to get authentication user!");
            e.printStackTrace();
        }

        return null;
    }

    private AuthUser createUser(AssiPlayer player) {
        AuthUser authUser = new AuthUser(player.getUuid());
        authUser.setLastIp(player.getIPs()[0]);
        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `auth` (uuid, last_ip) VALUES (?, ?);");
            preparedStatement.setString(1, authUser.getUuid().toString());
            preparedStatement.setString(2, authUser.getLastIp());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            getPlugin().getLogger().warning("Failed to create authentication user!");
            e.printStackTrace();
        }

        return authUser;
    }

    private void setSecret(AuthUser authUser) {
        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `auth` SET secret = ? WHERE uuid = ?");
            preparedStatement.setString(1, authUser.getSecret());
            preparedStatement.setString(2, authUser.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            getPlugin().getLogger().warning("Failed to set the secret to " + authUser.getUuid() + "!");
            e.printStackTrace();
        }
    }

    private void remind(CommandSender player) {
        player.sendMessage(C.II + ChatColor.BOLD.toString() + "This account requires authentication.");
        player.sendMessage(C.II + "Please open your Google Authenticator App and post the code in chat.");
        player.sendMessage(C.II + "Failure to comply with result in an ban.");
    }

}
