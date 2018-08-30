package net.assimilationmc.assicore.anticheat;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.hook.AssiHook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.reflex.api.CheckResult;
import rip.reflex.api.ReflexAPI;
import rip.reflex.api.ReflexAPIProvider;
import rip.reflex.api.event.ReflexCheckEvent;
import rip.reflex.api.event.ReflexLoadEvent;

public class HookReflex implements AssiHook<ReflexAPI>, Listener {

    private final AssiPlugin plugin;
    private ReflexAPI reflexAPI;

    public HookReflex(AssiPlugin plugin) {
        this.plugin = plugin;

        plugin.registerListener(this);
    }

    @EventHandler
    public void on(final ReflexLoadEvent e) {
        this.reflexAPI = ReflexAPIProvider.getAPI();
    }

    @EventHandler
    public void on(final ReflexCheckEvent e) {
        final Player player = e.getPlayer();
        final CheckResult result = e.getResult();

        if (result.isCheckPassed()) return;

        final int ping = reflexAPI.getPing(player);

        plugin.getStaffChatManager().messageGenericLocal(ChatColor.AQUA + "[AntiCheat] " +
                ChatColor.RED + player.getName() + ChatColor.GRAY + " failed " + ChatColor.RED + ChatColor.BOLD + e.getCheat().name() +
                ChatColor.GRAY + " (VL " + ChatColor.RED + reflexAPI.getViolations(player, e.getCheat()) + ChatColor.GRAY + " +" + result.getViolationsMod() + ") "
                + ChatColor.ITALIC + "Ping: " + ping + "ms");
    }

    @Override
    public ReflexAPI getHook() {
        return reflexAPI;
    }

}
