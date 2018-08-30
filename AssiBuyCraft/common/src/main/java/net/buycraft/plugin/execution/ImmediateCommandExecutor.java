package net.buycraft.plugin.execution;

import lombok.RequiredArgsConstructor;
import net.buycraft.plugin.IBuycraftPlatform;
import net.buycraft.plugin.client.ApiException;
import net.buycraft.plugin.data.QueuedCommand;
import net.buycraft.plugin.data.responses.QueueInformation;
import net.buycraft.plugin.execution.strategy.ToRunQueuedCommand;

import java.io.IOException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ImmediateCommandExecutor implements Runnable {

    private final IBuycraftPlatform platform;

    @Override
    public void run() {
        if (platform.getApiClient() == null) {
            return; // no API client
        }

        QueueInformation information;

        try {
            // Retrieve offline command queue.
            information = platform.getApiClient().retrieveOfflineQueue();
        } catch (IOException | ApiException e) {
            platform.log(Level.SEVERE, "Could not fetch command queue", e);
            return;
        }

        // Queue commands for later.
        for (QueuedCommand command : information.getCommands()) {
            platform.getExecutor().queue(new ToRunQueuedCommand(command.getPlayer(), command, false));
        }
    }
}
