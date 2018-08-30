package net.assimilationmc.assicore.patch;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PatchWDL implements AssiPatch, PluginMessageListener {

    private final AssiPlugin plugin;

    public PatchWDL(AssiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "WDL|INIT", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "WDL|CONTROL");

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "PERMISSIONSREPL", this);

    }

    @Override
    public void unregister() {
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "WDL|INIT");
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, "WDL|CONTROL");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equals("WDL|INIT") || channel.equals("PERMISSIONSREPL")) {
            if (channel.equals("PERMISSIONSREPL") && !new String(bytes).equals("mod.worlddownloader")) {
                return;
            }

            plugin.getPunishmentManager().punish(plugin.getPunishmentManager().getConsole(), plugin.getPlayerManager().getPlayer(player), PunishmentCategory.CLIENT, "[AUTO] " +
                    "Use of a World Downloader");
        }
    }

}
