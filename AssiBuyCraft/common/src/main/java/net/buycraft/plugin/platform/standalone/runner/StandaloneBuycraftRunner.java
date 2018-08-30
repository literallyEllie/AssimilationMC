package net.buycraft.plugin.platform.standalone.runner;

import lombok.Getter;
import net.buycraft.plugin.IBuycraftPlatform;
import net.buycraft.plugin.client.ApiException;
import net.buycraft.plugin.client.ProductionApiClient;
import net.buycraft.plugin.data.QueuedCommand;
import net.buycraft.plugin.data.QueuedPlayer;
import net.buycraft.plugin.data.responses.ServerInformation;
import net.buycraft.plugin.event.DonationUpdateType;
import net.buycraft.plugin.execution.DuePlayerFetcher;
import net.buycraft.plugin.platform.NoBlocking;
import net.buycraft.plugin.platform.standalone.StandaloneBuycraftPlatform;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StandaloneBuycraftRunner {
    private final CommandDispatcher dispatcher;
    private final PlayerDeterminer determiner;
    private final String apiKey;
    private final Logger logger;
    private final ScheduledExecutorService executorService;
    private final IBuycraftPlatform platform;
    private final boolean verbose;
    @Getter
    private ServerInformation serverInformation;
    @Getter
    private DuePlayerFetcher playerFetcher;

    StandaloneBuycraftRunner(CommandDispatcher dispatcher, PlayerDeterminer determiner, String apiKey, Logger logger, ScheduledExecutorService executorService, boolean verbose) {
        this.dispatcher = dispatcher;
        this.determiner = determiner;
        this.apiKey = apiKey;
        this.logger = logger;
        this.executorService = executorService;
        this.platform = new Platform();
        this.verbose = verbose;
    }

    public void initializeTasks() {
        try {
            serverInformation = platform.getApiClient().getServerInformation();
        } catch (IOException | ApiException e) {
            throw new RuntimeException("Can't fetch account information", e);
        }
        executorService.schedule(playerFetcher = new DuePlayerFetcher(platform, verbose), 1, TimeUnit.SECONDS);
    }

    @NoBlocking
    private class Platform extends StandaloneBuycraftPlatform {
        Platform() {
            super(new ProductionApiClient(apiKey, new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build()), executorService);
        }

        @Override
        public void dispatchCommand(String command) {
            dispatcher.dispatchCommand(command);
        }

        @Override
        public boolean isPlayerOnline(QueuedPlayer player) {
            return determiner.isPlayerOnline(player);
        }

        @Override
        public int getFreeSlots(QueuedPlayer player) {
            return determiner.getFreeSlots(player);
        }

        @Override
        public void log(Level level, String message) {
            logger.log(level, message);
        }

        @Override
        public void log(Level level, String message, Throwable throwable) {
            logger.log(level, message, throwable);
        }

        @Override
        public ServerInformation getServerInformation() {
            return serverInformation;
        }

        @Override
        public void callDonationEvent(QueuedPlayer queuedPlayer, QueuedCommand command, DonationUpdateType donationUpdateType) {

        }
    }
}
