package net.assimilationmc.assibungee.stafflog;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StaffLoggerManager extends Module {

    private Map<UUID, StaffLog> staffLogMap;
    private Set<UUID> recentlyConnected;

    private File directory;

    public StaffLoggerManager(AssiBungee assiBungee) {
        super(assiBungee, "Staff Logger");
    }

    @Override
    protected void start() {
        this.staffLogMap = Maps.newHashMap();
        this.recentlyConnected = Sets.newHashSet();

        this.directory = new File(getPlugin().getDataFolder(), "staff-log");
        if (!directory.isDirectory())
            directory.mkdirs();

    }

    @Override
    protected void end() {
        staffLogMap.values().forEach(StaffLog::cleanup);
        staffLogMap.clear();
    }

    @EventHandler(priority = 64)
    public void on(final PostLoginEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission("staff.track")) return;

        StaffLog log = getLog(player.getUniqueId());
        if (log == null) createLog(player.getUniqueId());

        recentlyConnected.add(player.getUniqueId());
        getPlugin().getProxy().getScheduler().schedule(getPlugin(), () -> recentlyConnected.remove(player.getUniqueId()), 1, TimeUnit.SECONDS);

    }

    @EventHandler(priority = 64)
    public void on(final ServerSwitchEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission("staff.track")) return;

        StaffLog log = getLog(player.getUniqueId());
        if (player.getServer() == null) return;

        getPlugin().getProxy().getScheduler().schedule(getPlugin(), () -> {
            if (recentlyConnected.contains(player.getUniqueId())) {
                log.write("LOGIN", player.getName() + " logged in to " + player.getServer().getInfo().getName(), true);
                log.setStart(System.currentTimeMillis());
            } else
                log.write("SWITCH", "-> " + player.getServer().getInfo().getName());

        }, 30, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void on(final ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;

        final ProxiedPlayer player = ((ProxiedPlayer) e.getSender());
        if (!player.hasPermission("staff.track")) return;

        StaffLog log = getLog(player.getUniqueId());
        log.write((e.isCommand() ? "COMMAND" : "CHAT"), e.getMessage());
    }

    @EventHandler
    public void on(final PlayerDisconnectEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission("staff.track")) return;

        final StaffLog log = getLog(player.getUniqueId());

        long length = System.currentTimeMillis() - log.getStart();
        log.write("DISCONNECT", "After " + TimeUnit.MILLISECONDS.toSeconds(length) + "s\n" +
                "---------------------------", true);

        log.cleanup();
        staffLogMap.remove(player.getUniqueId());
    }

    public StaffLog getLog(UUID uuid) {
        if (staffLogMap.containsKey(uuid))
            return staffLogMap.get(uuid);

        for (File file : directory.listFiles()) {
            if (file.getName().equals(uuid.toString() + ".txt")) {

                StaffLog staffLog = new StaffLog(uuid, file);
                staffLogMap.put(uuid, staffLog);
                return staffLog;
            }
        }

        final StaffLog log = createLog(uuid);
        log.write("SET", "Mid-session log creation successful.");
        return log;
    }

    public StaffLog createLog(UUID uuid) {
        if (staffLogMap.containsKey(uuid)) return staffLogMap.get(uuid);

        final StaffLog value = new StaffLog(uuid, new File(directory, uuid.toString() + ".txt"));
        staffLogMap.put(uuid, value);
        return value;
    }


}
