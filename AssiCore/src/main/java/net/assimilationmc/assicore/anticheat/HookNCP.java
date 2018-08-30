package net.assimilationmc.assicore.anticheat;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.components.NoCheatPlusAPI;
import fr.neatmonster.nocheatplus.hooks.AbstractNCPHook;
import fr.neatmonster.nocheatplus.hooks.ILast;
import fr.neatmonster.nocheatplus.hooks.IStats;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.hook.AssiHook;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HookNCP extends AbstractNCPHook implements AssiHook<NoCheatPlusAPI>, IStats, ILast {

    private final AssiPlugin plugin;

    public HookNCP(AssiPlugin plugin) {
        this.plugin = plugin;

        NCPHookManager.addHook(CheckType.ALL, this);
    }

    @Override
    public NoCheatPlusAPI getHook() {
        return NCPAPIProvider.getNoCheatPlusAPI();
    }

    @Override
    public String getHookName() {
        return "AssimilationMC";
    }

    @Override
    public String getHookVersion() {
        return "1.0";
    }

    @Override
    public boolean onCheckFailure(CheckType checkType, Player player, IViolationInfo info) {

        plugin.getStaffChatManager().messageGenericLocal(ChatColor.AQUA + "[N-AntiCheat] " +
                ChatColor.RED + player.getName() + ChatColor.GRAY + " failed " + ChatColor.RED + ChatColor.BOLD + checkType.getName() +
                ChatColor.GRAY + " (VL " + ChatColor.RED + Math.round(info.getTotalVl()) + ChatColor.GRAY + " +" + Math.round(info.getAddedVl()) + ") "
                + ChatColor.ITALIC + "Ping: " + ((CraftPlayer) player).getHandle().ping + "ms");

        return false;
    }

}
