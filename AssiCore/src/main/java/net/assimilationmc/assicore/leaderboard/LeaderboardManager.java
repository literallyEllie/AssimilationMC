package net.assimilationmc.assicore.leaderboard;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.cosmetic.Cosmetic;
import net.assimilationmc.assicore.cosmetic.cosmetics.particle.CosmeticDiamonds;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Callback;
import net.assimilationmc.assicore.util.SerializedLocation;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LeaderboardManager extends Module {

    private File file;
    private YamlConfiguration configuration;

    private Map<Integer, LeaderboardEntity> leaderboardEntities;
    private Map<UUID, Callback<Location>> signLocationSetter;

    private Cosmetic cosmeticDiamonds;

    public LeaderboardManager(AssiPlugin plugin) {
        super(plugin, "Leaderboard Manager");
    }

    @Override
    protected void start() {
        this.file = new File(getPlugin().getDataFolder(), "leaderboards.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log(Level.SEVERE, "Failed to create leaderboards.yml");
                e.printStackTrace();
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.isConfigurationSection("lb")) {
            configuration.createSection("lb");
        }

        this.signLocationSetter = Maps.newHashMap();
        this.leaderboardEntities = Maps.newHashMap();

        for (String s : configuration.getConfigurationSection("lb").getKeys(false)) {
            int id = Integer.parseInt(s);
            Location location = SerializedLocation.deserialize(configuration.getString("lb." + s + ".updateSign")).toLocation();
            leaderboardEntities.put(id, new LeaderboardEntity(id, location, configuration.getString("lb." + s + ".type"),
                    Integer.parseInt(configuration.getString("lb." + s + ".place"))));
        }


        getPlugin().getCommandManager().registerCommand(new CmdLeaderboardAdmin(this));

        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), this::setCosmetic, 60L);

    }

    @Override
    protected void end() {

        this.signLocationSetter.clear();
        this.leaderboardEntities.clear();

    }

    public void create(Player player, int citizensId, String type, int place) {
        if (leaderboardEntities.containsKey(citizensId)) {
            player.sendMessage(C.II + "Leaderboard entity with that ID exists.");
            return;
        }

        LeaderboardEntity leaderboardEntity = new LeaderboardEntity(citizensId);
        leaderboardEntity.setLeaderboardType(type);
        leaderboardEntity.setPlace(place);

        player.sendMessage(C.II + "Right click a sign to finish.");
        signLocationSetter.put(player.getUniqueId(), data -> {
            leaderboardEntity.setUpdateSign(data);
            leaderboardEntities.put(citizensId, leaderboardEntity);

            configuration.set("lb." + citizensId + ".updateSign", new SerializedLocation(data, false).toString());
            configuration.set("lb." + citizensId + ".type", type);
            configuration.set("lb." + citizensId + ".place", place);
            save();

            player.sendMessage(C.C + "Done.");
        });

    }

    public void delete(Player player, int citizensId) {
        if (!leaderboardEntities.containsKey(citizensId)) {
            player.sendMessage(C.II + "Leaderboard entity with that ID doesn't exist.");
            return;
        }

        configuration.set("lb." + citizensId, null);
        save();

        leaderboardEntities.remove(citizensId);
        player.sendMessage(C.C + "Removed.");

    }

    public Map<Integer, LeaderboardEntity> getLeaderboardEntities() {
        return leaderboardEntities;
    }

    public List<LeaderboardEntity> getLeaderboardsOfType(String type) {
        return leaderboardEntities.values().stream().filter(leaderboardEntity -> leaderboardEntity.getLeaderboardType().equalsIgnoreCase(type)).
                sorted(Comparator.comparing(LeaderboardEntity::getPlace)).collect(Collectors.toList());
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        if (!signLocationSetter.containsKey(e.getPlayer().getUniqueId()) || e.getClickedBlock() == null) return;

        final Block clickedBlock = e.getClickedBlock();
        if (!clickedBlock.getType().name().contains("SIGN")) {
            e.getPlayer().sendMessage(C.II + "Not a sign.");
            return;
        }

        signLocationSetter.get(e.getPlayer().getUniqueId()).callback(clickedBlock.getLocation());
        signLocationSetter.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() != UpdateType.SEC || cosmeticDiamonds == null) return;
        List<LeaderboardEntity> leaderboardEntities = getLeaderboardEntities().values().stream().filter(leaderboardEntity -> leaderboardEntity.getPlace() == 1).collect(Collectors.toList());
        if (leaderboardEntities.isEmpty()) return;

        for (LeaderboardEntity leaderboardEntity : leaderboardEntities) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(leaderboardEntity.getCitizensId());
            if (npc == null) continue;
            cosmeticDiamonds.tick(npc.getStoredLocation());
        }

    }

    private void setCosmetic() {
        cosmeticDiamonds = getPlugin().getCosmeticManager().getCosmetic(CosmeticDiamonds.class);
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to save leaderboards.yml");
            e.printStackTrace();
        }
    }

}
