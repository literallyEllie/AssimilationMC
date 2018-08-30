package net.assimilationmc.assibungee;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.assimilationmc.assibungee.announce.AnnounceManager;
import net.assimilationmc.assibungee.command.BungeeCommandManager;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.donate.DonationHandler;
import net.assimilationmc.assibungee.internal.InternalListeners;
import net.assimilationmc.assibungee.internal.OneWayPingHandle;
import net.assimilationmc.assibungee.mysql.SQLManager;
import net.assimilationmc.assibungee.party.BungeePartyCleaner;
import net.assimilationmc.assibungee.player.BungeePlayerManager;
import net.assimilationmc.assibungee.redis.BungeeRedisManager;
import net.assimilationmc.assibungee.server.balancer.BalancerManager;
import net.assimilationmc.assibungee.server.data.GeneralServerPropertyReader;
import net.assimilationmc.assibungee.server.data.ServerData;
import net.assimilationmc.assibungee.stafflog.StaffLoggerManager;
import net.assimilationmc.assibungee.vote.VoteManager;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class AssiBungee extends Plugin {

    /* Modules */
    private final Collection<Module> modules = Lists.newArrayList();
    /* Server request */
    private ServerData serverData;
    private long pluginStart, pluginFinishStart;
    private SQLManager sqlManager;
    private BungeeRedisManager redisManager;

    private BungeePlayerManager playerManager;
    private BungeeCommandManager bungeeCommandManager;
    private DiscordManager discordManager;
    private BalancerManager balancerManager;
    private AnnounceManager annouceManager;
    private BungeePartyCleaner partyCleaner;
    private VoteManager voteManager;
    private DonationHandler donationHandler;
    private StaffLoggerManager loggerManager;

    private OneWayPingHandle oneWayPingHandle;

    private Set<UUID> messageOff;

    @Override
    public void onEnable() {
        pluginStart = System.currentTimeMillis();

        final File serverFile = new File("SERVER_DATA");
        if (!serverFile.exists()) {
            try {
                if (!serverFile.createNewFile()) throw new RuntimeException("Failed to create SERVER_DATA file!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Invalid server request! (Doesn't exist!) Please fill in!");
        }

        serverData = new GeneralServerPropertyReader(serverFile).readServerData();

        if (!getDataFolder().isDirectory()) getDataFolder().mkdirs();

        try {

            this.sqlManager = new SQLManager(this);
            this.redisManager = new BungeeRedisManager(this);

            this.playerManager = new BungeePlayerManager(this);

            this.oneWayPingHandle = new OneWayPingHandle(this);
            this.bungeeCommandManager = new BungeeCommandManager(this);

            if (DiscordManager.FILE.exists()) {
                this.discordManager = new DiscordManager(this);
            }

            this.balancerManager = new  BalancerManager(this);
            this.partyCleaner = new BungeePartyCleaner(this);
            this.annouceManager = new AnnounceManager(this);
            if (getProxy().getPluginManager().getPlugin("NuVotifier") != null) {
                this.voteManager = new VoteManager(this);
            }
            if (getProxy().getPluginManager().getPlugin("BuycraftX") != null) {
                this.donationHandler = new DonationHandler(this);
            }

            this.loggerManager = new StaffLoggerManager(this);

        } catch (Throwable e) {
            log(Level.SEVERE, "Failure loading modules!");
            e.printStackTrace();
        }


        new InternalListeners(this);

        this.pluginFinishStart = System.currentTimeMillis();
        log("Plugin started in " + (pluginFinishStart - pluginStart) + "ms");

        messageOff = Sets.newHashSet();
    }

    @Override
    public void onDisable() {
        modules.forEach(module -> {
            if (!isVital(module))
                module.end();
        });

        modules.forEach(Module::end);
        modules.clear();
    }

    /**
     * Quick-access listener register
     *
     * @param listener Listener to register
     */
    public void registerListener(Listener listener) {
        getProxy().getPluginManager().registerListener(this, listener);
    }

    /**
     * @return Modules
     */
    public Collection<Module> getModules() {
        return modules;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public BungeeRedisManager getRedisManager() {
        return redisManager;
    }

    public BungeePlayerManager getPlayerManager() {
        return playerManager;
    }

    public BungeeCommandManager getBungeeCommandManager() {
        return bungeeCommandManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public BalancerManager getBalancerManager() {
        return balancerManager;
    }

    public AnnounceManager getAnnouceManager() {
        return annouceManager;
    }

    public BungeePartyCleaner getPartyCleaner() {
        return partyCleaner;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public DonationHandler getDonationHandler() {
        return donationHandler;
    }

    public long getPluginStart() {
        return pluginStart;
    }

    public long getPluginFinishStart() {
        return pluginFinishStart;
    }

    public StaffLoggerManager getLoggerManager() {
        return loggerManager;
    }

    public OneWayPingHandle getOneWayPingHandle() {
        return oneWayPingHandle;
    }

    private void log(String message) {
        log(Level.INFO, message);
    }

    private void log(Level level, String message) {
        getLogger().log(level, message);
    }

    private boolean isVital(Module module) {
        return module instanceof SQLManager
                || module instanceof BungeeRedisManager;
    }

    public Set<UUID> getMessageOff() {
        return messageOff;
    }

}
