package net.buycraft.plugin.execution.strategy;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.buycraft.plugin.IBuycraftPlatform;
import net.buycraft.plugin.data.QueuedCommand;
import net.buycraft.plugin.data.QueuedPlayer;
import net.buycraft.plugin.event.DonationUpdateType;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Value
@EqualsAndHashCode(exclude = {"queueTime"})
public class ToRunQueuedCommand {

    private final QueuedPlayer player;
    private final QueuedCommand command;
    private final boolean requireOnline;
    private final long queueTime = System.currentTimeMillis();

    public boolean canExecute(IBuycraftPlatform platform) {
        Integer requiredSlots = command.getConditions().get("slots");

        if (requiredSlots != null || requireOnline) {
            if (!platform.isPlayerOnline(player)) {
                return false;
            }
        }

        if (requiredSlots != null) {
            int free = platform.getFreeSlots(player);
            if (free < requiredSlots) {
                return false;
            }
        }

        Integer delay = command.getConditions().get("delay");
        final boolean b = !(delay != null && delay > 0 && System.currentTimeMillis() - queueTime < TimeUnit.SECONDS.toMillis(delay));
        if (b) {
            DonationUpdateType updateType;
            try {
                updateType = DonationUpdateType.valueOf(command.getCommand().toUpperCase());
            } catch (IllegalArgumentException e) {
                platform.log(Level.SEVERE, "Invalid donation event action " + command.getCommand() + " for payment ID " + command.getPaymentId());
                return false;
            }

            platform.callDonationEvent(player, command, updateType);
        }

        return b;
    }
}
