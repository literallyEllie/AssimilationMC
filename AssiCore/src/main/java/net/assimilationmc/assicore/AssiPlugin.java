package net.assimilationmc.assicore;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.achievement.AchievementManager;
import net.assimilationmc.assicore.anticheat.HookNCP;
import net.assimilationmc.assicore.anticheat.HookReflex;
import net.assimilationmc.assicore.auth.AuthManager;
import net.assimilationmc.assicore.booster.BoosterManager;
import net.assimilationmc.assicore.chat.ChatManager;
import net.assimilationmc.assicore.citizens.NPCManager;
import net.assimilationmc.assicore.command.CommandManager;
import net.assimilationmc.assicore.command.commands.MicroCommand;
import net.assimilationmc.assicore.cosmetic.CosmeticManager;
import net.assimilationmc.assicore.donate.DonationManager;
import net.assimilationmc.assicore.event.update.Updater;
import net.assimilationmc.assicore.friend.FriendManager;
import net.assimilationmc.assicore.helpop.HelpOPManager;
import net.assimilationmc.assicore.internal.DiscordCommunicator;
import net.assimilationmc.assicore.internal.InternalPingHandle;
import net.assimilationmc.assicore.internal.LocalAnnouncementSetting;
import net.assimilationmc.assicore.internal.SpecialHeadListener;
import net.assimilationmc.assicore.joinitems.JoinItemManager;
import net.assimilationmc.assicore.leaderboard.LeaderboardManager;
import net.assimilationmc.assicore.lobby.LobbyManager;
import net.assimilationmc.assicore.mysql.SQLManager;
import net.assimilationmc.assicore.party.PartyManager;
import net.assimilationmc.assicore.patch.PatchManager;
import net.assimilationmc.assicore.player.PlayerFinder;
import net.assimilationmc.assicore.player.PlayerManager;
import net.assimilationmc.assicore.player.PlayerSynchronizer;
import net.assimilationmc.assicore.punish.PunishmentManager;
import net.assimilationmc.assicore.rank.CmdSetRank;
import net.assimilationmc.assicore.redis.RedisManager;
import net.assimilationmc.assicore.reward.RewardManager;
import net.assimilationmc.assicore.scoreboard.ScoreboardManager;
import net.assimilationmc.assicore.server.GeneralServerPropertyReader;
import net.assimilationmc.assicore.server.ServerData;
import net.assimilationmc.assicore.server.TPSTask;
import net.assimilationmc.assicore.server.analytics.AnalyticsManager;
import net.assimilationmc.assicore.staff.StaffChatManager;
import net.assimilationmc.assicore.ui.UIManager;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.Domain;
import net.assimilationmc.assicore.vote.VoteManager;
import net.assimilationmc.assicore.web.WebAPIManager;
import net.assimilationmc.assicore.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;

public abstract class AssiPlugin extends JavaPlugin {

    /* Modules */
    private final Collection<Module> modules = Lists.newArrayList();
    /* Server request */
    private ServerData serverData;
    private long pluginStart, pluginFinishStart;
    private SQLManager sqlManager;
    private RedisManager redisManager;

    private CommandManager commandManager;
    private PlayerManager playerManager;

    private AnalyticsManager analyticsManager;
    private WebAPIManager webAPIManager;

    private ScoreboardManager scoreboardManager;
    private HelpOPManager helpOPManager;
    private RewardManager rewardManager;
    private AchievementManager achievementManager;
    private ChatManager chatManager;
    private LeaderboardManager leaderboardManager;
    private StaffChatManager staffChatManager;
    private JoinItemManager joinItemManager;
    private LobbyManager lobbyManager;
    private WorldManager worldManager;
    private AuthManager authManager;
    private UIManager uiManager;
    private PunishmentManager punishmentManager;
    private NPCManager npcManager;
    private VoteManager voteManager;
    private DonationManager donationManager;
    private PartyManager partyManager;
    private PatchManager patchManager;
    private CosmeticManager cosmeticManager;
    private FriendManager friendManager;
    private BoosterManager boosterManager;

    private LocalAnnouncementSetting localAnnouncementSetting;
    private DiscordCommunicator discordCommunicator;
    private PlayerFinder playerFinder;
    private InternalPingHandle internalPingHandle;
    private TPSTask tpsTask;

    @Override
    public final void onEnable() {
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

        log("Server is in " + (serverData.isDev() ? "DEV" : "NORMAL") + " mode " + (serverData.isLocal() ? "(Local)" : ""));
        if (serverData.hasRequiredRank()) log("Server requires rank " + serverData.getRequiredRank() + " to join");
        else log("Any rank can join this server.");

        if (!getDataFolder().isDirectory()) getDataFolder().mkdirs();

        /* Module loaders */
        try {

            if (SQLManager.FILE.exists()) {
                sqlManager = new SQLManager(this);
            } else log(Level.WARNING, "SQL_SERVER file not found, not enabling SQL Manager.");

//            if (RedisManager.FILE.exists()) {
            redisManager = new RedisManager(this);
//            }

            commandManager = new CommandManager(this);
            playerManager = new PlayerManager(this);

            if (serverData.isAnalytics()) {
                analyticsManager = new AnalyticsManager(this);
            }

            this.webAPIManager = new WebAPIManager(this);

            this.internalPingHandle = new InternalPingHandle(this);
            this.scoreboardManager = new ScoreboardManager(this);

            helpOPManager = new HelpOPManager(this);
            rewardManager = new RewardManager(this);
            uiManager = new UIManager(this);
            achievementManager = new AchievementManager(this);
            chatManager = new ChatManager(this);
            staffChatManager = new StaffChatManager(this);
            joinItemManager = new JoinItemManager(this);
            worldManager = new WorldManager(this);
            leaderboardManager = new LeaderboardManager(this);
            lobbyManager = new LobbyManager(this);
            punishmentManager = new PunishmentManager(this);
            npcManager = new NPCManager(this);
            voteManager = new VoteManager(this);
            donationManager = new DonationManager(this);
            partyManager = new PartyManager(this);
            patchManager = new PatchManager(this);
            cosmeticManager = new CosmeticManager(this);
            friendManager = new FriendManager(this);
            boosterManager = new BoosterManager(this);

            this.tpsTask = new TPSTask();
            getServer().getScheduler().runTaskTimer(this, tpsTask, 50, 50);

            if (!(serverData.isDev() || serverData.isLocal())) {
                authManager = new AuthManager(this);
            }

        } catch (Throwable e) {
            log(Level.SEVERE, "Failure loading modules!");
            e.printStackTrace();
        }

        new Updater(this);
        new PlayerSynchronizer(this);

        this.localAnnouncementSetting = new LocalAnnouncementSetting(this);
        this.discordCommunicator = new DiscordCommunicator(this);
        this.playerFinder = new PlayerFinder(this);
        registerListener(new SpecialHeadListener());

        registerInternalCommands();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (getServer().getPluginManager().getPlugin("Reflex") != null) {
            log(Level.INFO, "Reflex anti cheat detected. Hooking...");
            new HookReflex(this);
        }

        if (getServer().getPluginManager().getPlugin("NoCheatPlus") != null) {
            log(Level.INFO, "NCP anti cheat detected. Hooking...");
           //  new HookNCP(this);
        }

        /* Begin dependant start */
        this.start();

        this.pluginFinishStart = System.currentTimeMillis();
        log("Plugin started in " + (pluginFinishStart - pluginStart) + "ms");
    }

    @Override
    public final void onDisable() {
        try {
            this.end();
        } catch (Throwable e) {
            log(Level.SEVERE, "Failed to shutdown child plugin!");
            e.printStackTrace();
        }

        modules.forEach(module -> {
            if (!isVital(module)) {
                try {
                    module.end();
                } catch (Throwable e) {
                    log(Level.SEVERE, "Error whilst disabling module " + module.getDisplay() + "!");
                    e.printStackTrace();
                }
            }
        });

        modules.removeIf(this::isVital);

        modules.forEach(Module::end);
        modules.clear();
    }

    protected abstract void start();

    protected abstract void end();

    /**
     * Quick-access listener register
     *
     * @param listener Listener to register
     */
    public final void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * @return Modules
     */
    public final Collection<Module> getModules() {
        return modules;
    }

    public final ServerData getServerData() {
        return serverData;
    }

    public final long getPluginStart() {
        return pluginStart;
    }

    public final long getPluginFinishStart() {
        return pluginFinishStart;
    }

    public final SQLManager getSqlManager() {
        return sqlManager;
    }

    public final RedisManager getRedisManager() {
        return redisManager;
    }

    public final PlayerManager getPlayerManager() {
        return playerManager;
    }

    public WebAPIManager getWebAPIManager() {
        return webAPIManager;
    }

    public InternalPingHandle getInternalPingHandle() {
        return internalPingHandle;
    }

    public final CommandManager getCommandManager() {
        return commandManager;
    }

    public final ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public final HelpOPManager getHelpOPManager() {
        return helpOPManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public final AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public final ChatManager getChatManager() {
        return chatManager;
    }

    public final StaffChatManager getStaffChatManager() {
        return staffChatManager;
    }

    public final UIManager getUiManager() {
        return uiManager;
    }

    public final JoinItemManager getJoinItemManager() {
        return joinItemManager;
    }

    public final AnalyticsManager getAnalyticsManager() {
        return analyticsManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public final LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public final WorldManager getWorldManager() {
        return worldManager;
    }

    public final AuthManager getAuthManager() {
        return authManager;
    }

    public final PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public final NPCManager getNpcManager() {
        return npcManager;
    }

    public final VoteManager getVoteManager() {
        return voteManager;
    }

    public final DonationManager getDonationManager() {
        return donationManager;
    }

    public final PartyManager getPartyManager() {
        return partyManager;
    }

    public final PatchManager getPatchManager() {
        return patchManager;
    }

    public final CosmeticManager getCosmeticManager() {
        return cosmeticManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    public final LocalAnnouncementSetting getLocalAnnouncementSetting() {
        return localAnnouncementSetting;
    }

    public final DiscordCommunicator getDiscordCommunicator() {
        return discordCommunicator;
    }

    public final PlayerFinder getPlayerFinder() {
        return playerFinder;
    }

    private void log(String message) {
        log(Level.INFO, message);
    }

    private void log(Level level, String message) {
        getLogger().log(level, message);
    }

    private void registerInternalCommands() {
        getCommandManager().registerCommand(new CmdSetRank(this));

        getCommandManager().registerCommand(
                new MicroCommand(this, "discord", "You can find our Discord at " + C.V + Domain.DISCORD, Lists.newArrayList()),
                new MicroCommand(this, "web", "Our website is at " + C.V + Domain.PROT_WEB, Lists.newArrayList("website")),
                new MicroCommand(this, "forums", "Our community forums can be found at " + C.V + Domain.PROT_FORUM, Lists.newArrayList("forum")),
                new MicroCommand(this, "store", "Thanks so much for being interested in helping to support AssimilationMC! " +
                        "You can find our Donator store at " + C.V + Domain.PROT_STORE, Lists.newArrayList("donate", "xoxo")),
                new MicroCommand(this, "help", "You can see what commands you can run at " + C.V + Domain.LINK_HELP,
                        Lists.newArrayList("commands")));
    }

    public TPSTask getTpsTask() {
        return tpsTask;
    }

    private boolean isVital(Module module) {
        return module instanceof SQLManager
                || module instanceof RedisManager;
    }

}