package net.assimilationmc.assiuhc.team.reconnect;

import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class ReconnectData {

    private UUID uuid;
    private String name;
    private Location location;
    private PlayerInventory playerInventory;

    public ReconnectData(UUID uuid, String name, Location location, PlayerInventory playerInventory) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.playerInventory = playerInventory;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

}
