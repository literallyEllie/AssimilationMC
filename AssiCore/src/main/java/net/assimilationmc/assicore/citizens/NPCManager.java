package net.assimilationmc.assicore.citizens;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.hook.AssiHook;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilTime;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class NPCManager extends Module implements AssiHook<NPCRegistry> {

    private File eventFile;
    private NPCRegistry npcs;
    private Map<Integer, CitizenHookData> citizenDataMap;

    private Map<Integer, Map<UUID, Long>> lastUse;

    public NPCManager(AssiPlugin plugin) {
        super(plugin, "NPC Manager");
    }

    @Override
    protected void start() {
        if (!getPlugin().getServer().getPluginManager().isPluginEnabled("Citizens")) return;

        eventFile = new File(getPlugin().getDataFolder(), "citizens-hook-store.yml");

        if (!eventFile.exists()) {
            try {
                if (!eventFile.createNewFile()) throw new IOException();

                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(eventFile);
                configuration.createSection("data");
                configuration.save(eventFile);
            } catch (IOException e) {
                log(Level.SEVERE, "Failed to create/set defaults for " + eventFile.getName() + " file!");
                e.printStackTrace();
                return;
            }
        }

        npcs = CitizensAPI.getNPCRegistry();
        citizenDataMap = Maps.newHashMap();
        lastUse = Maps.newHashMap();

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(eventFile);
        for (String s : yamlConfiguration.getConfigurationSection("data").getValues(false).keySet()) {
            CitizenHookData citizenData = new CitizenHookData(Integer.parseInt(s));

            if (yamlConfiguration.isString("data." + s + ".message")) {
                citizenData.setMessage(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString("data." + s + ".message")));
            }

            if (yamlConfiguration.isString("data." + s + ".commandExec")) {
                citizenData.setCommandExec(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString("data." + s + ".commandExec")));
            }

            citizenDataMap.put(citizenData.getId(), citizenData);
        }

        getPlugin().getCommandManager().registerCommand(new CmdCitizenHandle(this));

    }

    @Override
    protected void end() {
        if (eventFile == null) return;

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(eventFile);

        for (CitizenHookData citizenData : citizenDataMap.values()) {
            if (citizenData.hasMessage()) {
                configuration.set("data." + citizenData.getId() + ".message", citizenData.getMessage().replace("§", "&"));
            }
            if (citizenData.hasCommand()) {
                configuration.set("data." + citizenData.getId() + ".commandExec", citizenData.getCommandExec().replace("§", "&"));
            }
        }

        try {
            configuration.save(eventFile);
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to save " + eventFile.getName() + "!");
            e.printStackTrace();
        }

        citizenDataMap.clear();
    }

    @Override
    public NPCRegistry getHook() {
        return npcs;
    }

    public Map<Integer, CitizenHookData> getCitizenDataMap() {
        return citizenDataMap;
    }

    public CitizenHookData getCitizenData(int id) {
        return citizenDataMap.get(id);
    }

    public boolean isCitizenData(int id) {
        return citizenDataMap.containsKey(id);
    }

    public void addCitizenHook(CitizenHookData citizenHookData) {
        if (isCitizenData(citizenHookData.getId()))
            citizenDataMap.remove(citizenHookData.getId());
        citizenDataMap.put(citizenHookData.getId(), citizenHookData);
    }

    public void delete(int id) {
        if (!isCitizenData(id)) return;

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(eventFile);
        configuration.set("data." + id, null);
        try {
            configuration.save(eventFile);
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to delete Citizen Hook request with id " + id);
            e.printStackTrace();
        }

        citizenDataMap.remove(id);

    }

    @EventHandler
    public void on(final PlayerInteractEntityEvent e) {
        if (handleInteract(e.getPlayer(), e.getRightClicked())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (handleInteract(((Player) e.getDamager()), e.getEntity())) e.setCancelled(true);
    }

    private boolean handleInteract(Player player, Entity entity) {
        final NPC npc = npcs.getNPC(entity);

        if (npc != null && citizenDataMap.containsKey(npc.getId())) {

            if (!canUse(player, npc.getId())) {
                player.sendMessage(C.II + "Please don't spam click me!");
                return true;
            }

            final CitizenHookData citizenHookData = citizenDataMap.get(npc.getId());
            if (citizenHookData.hasMessage()) {
                player.sendMessage(citizenHookData.getMessage().replace("{player}", player.getName()));
            }

            if (citizenHookData.hasCommand()) {
                getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(),
                        citizenHookData.getCommandExec().replace("{player}", player.getName()));
            }
            return true;
        }

        return false;
    }

    private boolean canUse(Player player, int npcId) {
        if (!lastUse.containsKey(npcId)) {
            Map<UUID, Long> a = Maps.newHashMap();
            a.put(player.getUniqueId(), UtilTime.now());
            lastUse.put(npcId, a);
            return true;
        }

        if (lastUse.get(npcId).containsKey(player.getUniqueId())) {
            long lastUseTime = lastUse.get(npcId).get(player.getUniqueId());
            if (UtilTime.elapsed(lastUseTime, 60)) { // 3 sec
                lastUse.get(npcId).replace(player.getUniqueId(), UtilTime.now());
                return true;
            }
            return false;
        }

        Map<UUID, Long> a = Maps.newHashMap();
        a.put(player.getUniqueId(), UtilTime.now());
        lastUse.put(npcId, a);
        return true;
    }

}
