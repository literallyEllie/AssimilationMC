package net.buycraft.plugin.bungeecord.event;

import net.buycraft.plugin.UuidUtil;
import net.buycraft.plugin.data.QueuedCommand;
import net.buycraft.plugin.data.QueuedPlayer;
import net.buycraft.plugin.event.DonationUpdateType;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class DonationUpdateEvent extends Event {

    private final int packageId;
    private final UUID playerUuid;
    private final String playerName;
    private final DonationUpdateType updateType;

    public DonationUpdateEvent(QueuedPlayer player, QueuedCommand queuedCommand, DonationUpdateType type) {
        this.packageId = queuedCommand.getPackageId();
        this.playerUuid = UuidUtil.mojangUuidToJavaUuid(player.getUuid());
        this.playerName = player.getName();
        this.updateType = type;
    }

    public int getPackageId() {
        return packageId;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public DonationUpdateType getUpdateType() {
        return updateType;
    }

}
