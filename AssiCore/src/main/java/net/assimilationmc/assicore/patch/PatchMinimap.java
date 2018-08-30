package net.assimilationmc.assicore.patch;

import net.assimilationmc.assicore.AssiPlugin;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PatchMinimap implements AssiPatch, Listener {

    private final AssiPlugin plugin;

    public PatchMinimap(AssiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        plugin.registerListener(this);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void onJoin(Player player) {

        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

//         Voxel map
        playerConnection.sendPacket(new PacketPlayOutChat(
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"\",\"extra\":[{\"text\":\"§3 §6 §3 §6 §3 §6 §e\"}]}")));
//         REI Map
        playerConnection.sendPacket(new PacketPlayOutChat(
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"\",\"extra\":[{\"text\":\"§0§0§1§2§3§5§e§f\"}]}")));
//         Damage indicators
        playerConnection.sendPacket(new PacketPlayOutChat(
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"\",\"extra\":[{\"text\":\"§0§0§c§d§e§f\"}]}")));


    }

}
