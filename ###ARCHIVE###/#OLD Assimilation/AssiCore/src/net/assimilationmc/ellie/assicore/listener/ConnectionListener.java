package net.assimilationmc.ellie.assicore.listener;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.api.ServerState;
import net.assimilationmc.ellie.assicore.api.text.TextUtils;
import net.assimilationmc.ellie.assicore.event.ScoreboardUpdateEvent;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.util.Channels;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConnectionListener implements Listener {

    private ModuleManager moduleManager = AssiCore.getCore().getModuleManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        AssiPlayer player1;
        try {
            player1 = ModuleManager.getModuleManager().getPlayerManager().loadOnlinePlayer(player, true);
        } catch (Exception ex) {
            Util.kickPlayer(player, "&cError whilst processing player information: " + ex.getCause() + ": " + ex.getMessage() + "\n &cThe " +
                    "issue has been recorded and it is recommended you contact staff for help");
            AssiDiscord.getAssiDiscord().messageChannel(Channels.BOT_LOGS, "**" + player.getName() + "** failed login at `" + ex.getCause() + "`: `" + ex.getMessage() + "`/`" + ex.getLocalizedMessage() + "`");
            ex.printStackTrace();
            return;
        }

        try {
            ModuleManager.getModuleManager().getPermissionManager().playerJoin(player);
        } catch (NullPointerException ex) {
        }

        if (player.isOp() && !moduleManager.getSecurityManager().checkPlayer(player.getUniqueId())) {
            player.setOp(false);
            player.sendMessage(Util.prefix() + Util.color("&cYou have been automatically deoped for security precautions."));
        }

        if (!AssiCore.getCore().getVanishedPlayers().isEmpty() && !player.hasPermission(PermissionLib.BYPASS.VANISH)) {
            AssiCore.getCore().getVanishedPlayers().forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    player.hidePlayer(p);
                }
            });
        }

        moduleManager.getScoreboardManager().createNewScore(e.getPlayer(), true);

        if (moduleManager.getConfigManager().isForceSpawn() && moduleManager.getConfigManager().getSpawn() != null) {
            player.teleport(moduleManager.getConfigManager().getSpawn().toLocation());
        }

        //e.setJoinMessage(moduleManager.getConfigManager().getJoinMessage(ChatColor.stripColor(Util.color(group.getPrefix().replace("%space%", "")))+player.getName()));
        e.setJoinMessage(null);

        for (int i = 0; i < 145; i++) {
            Util.mINFO_noP(player, "");
        }

        TextUtils.sendCenteredMessage(player, "&8&m------------------------------------------");
        TextUtils.sendCenteredMessage(player, "&8");
        TextUtils.sendCenteredMessage(player, "&cWelcome to &2Assi&amilation&c!");
        TextUtils.sendCenteredMessage(player, "&8");
        TextUtils.sendCenteredMessage(player, "&8&m------------------------------------------");

        for (int i = 0; i < 3; i++) {
            Util.mINFO_noP(player, "");
        }

        Util.mainLobby(player);

        if(player.hasPermission(PermissionLib.STAFF_CHAT)){
            ModuleManager.getModuleManager().getStaffChatManager().messageBypass(player.getName(), "&3has joined.");
        }

        Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(player.getName(), ScoreboardUpdateEvent.UpdateElement.ONLINE));
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e){
        if(AssiCore.getCore().getServerState() != ServerState.STABLE){
            if(!moduleManager.getConfigManager().getMaintenanceWhitelist().contains(e.getName().toLowerCase())){
                e.setKickMessage(Util.prefix()+"\n\n"+Util.color(moduleManager.getConfigManager().getMaintenanceReason()));
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        ModuleManager.getModuleManager().getPlayerManager().unloadPlayer(player.getName());
        ModuleManager.getModuleManager().getPermissionManager().playerLeave(player);

        if(player.isOp() && !moduleManager.getSecurityManager().checkPlayer(player.getUniqueId())){
            player.setOp(false);
        }

        if(AssiCore.getCore().getVanishedPlayers().contains(player.getUniqueId())){
            AssiCore.getCore().getVanishedPlayers().remove(player.getUniqueId());
        }

        e.setQuitMessage(moduleManager.getConfigManager().getLeaveMessage(e.getPlayer().getName()));
        Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(player.getName(), ScoreboardUpdateEvent.UpdateElement.ONLINE));
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent e){
        World from = e.getFrom();
        World to = e.getPlayer().getWorld();

        if(from != null && from != to){
            ModuleManager.getModuleManager().getPermissionManager().playerWorldSwitch(e.getPlayer());

        }

    }

}
