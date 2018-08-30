package net.assimilationmc.assicore.cosmetic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.cosmetic.command.CmdCosmetic;
import net.assimilationmc.assicore.cosmetic.command.MenuCosmetics;
import net.assimilationmc.assicore.cosmetic.cosmetics.inv.CosmeticBow;
import net.assimilationmc.assicore.cosmetic.cosmetics.inv.CosmeticFighter;
import net.assimilationmc.assicore.cosmetic.cosmetics.particle.CosmeticDiamonds;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class CosmeticManager extends Module {

    private List<Cosmetic> cosmetics;
    private Map<UUID, Set<Cosmetic>> activeCosmetics;

    private MenuCosmetics menuCosmetics;

    public CosmeticManager(AssiPlugin plugin) {
        super(plugin, "Cosmetic Manager");
    }

    @Override
    protected void start() {
        this.cosmetics = Lists.newArrayList();
        this.activeCosmetics = Maps.newHashMap();

        getPlugin().getCommandManager().registerCommand(new CmdCosmetic(getPlugin()));

//        registerCosmetic(new CosmeticLove());
        registerCosmetic(new CosmeticFighter());
        registerCosmetic(new CosmeticDiamonds(getPlugin()));
        registerCosmetic(new CosmeticBow(getPlugin()));

        this.menuCosmetics = new MenuCosmetics(getPlugin());

    }

    @Override
    protected void end() {
        cosmetics.clear();
        activeCosmetics.clear();
    }

    public void registerCosmetic(Cosmetic cosmetic) {
        cosmetics.add(cosmetic);
    }

    public void unregisterCosmetic(Cosmetic cosmetic) {
        cosmetics.remove(cosmetic);
    }

    public Cosmetic getCosmetic(Class clazz) {
        for (Cosmetic cosmetic : cosmetics) {
            if (cosmetic.getClass().equals(clazz)) {
                return cosmetic;
            }
        }
        return null;
    }

    public Set<Cosmetic> getPlayerCosmetics(Player player) {
        return activeCosmetics.get(player.getUniqueId());
    }

    public void playPlayerCosmetic(Player player, CosmeticType cosmeticType) {
        this.playPlayerCosmetic(player, getCosmetic(cosmeticType.getClazz()));
    }

    public void playPlayerCosmetic(Player player, Cosmetic desired) {
        if (desired == null) return;
        if (activeCosmetics.containsKey(player.getUniqueId())) {
            final Set<Cosmetic> cosmetics = getPlayerCosmetics(player);

            final Iterator<Cosmetic> iterator = cosmetics.iterator();
            iterator.forEachRemaining(cosmetic -> {
                if (cosmetic.conflictsWith(desired.getClass())) {
                    player.sendMessage(C.II + "The cosmetic " + C.V + cosmetic.getName() +
                            C.II + " has been disabled as it conflicts with " + C.V + desired.getName() + C.II + ".");
                    cosmetic.remove(player);
                    iterator.remove();
                }
            });
        } else activeCosmetics.put(player.getUniqueId(), Sets.newHashSet());

        activeCosmetics.get(player.getUniqueId()).add(desired);
        desired.apply(player);
    }

    public void removeAllPlayerCosmetic(Player player) {
        if (!activeCosmetics.containsKey(player.getUniqueId())) return;
        activeCosmetics.get(player.getUniqueId()).forEach(cosmetic -> cosmetic.remove(player));
        activeCosmetics.remove(player.getUniqueId());
        player.sendMessage(C.II + "All your cosmetics have been disabled.");
    }

    public void removePlayerCosmetic(Player player, CosmeticType cosmeticType) {
        if (activeCosmetics.containsKey(player.getUniqueId())) {

            Cosmetic cosmetic = getCosmetic(cosmeticType.getClazz());
            if (cosmetic != null) {
                final Set<Cosmetic> cosmetics = activeCosmetics.get(player.getUniqueId());
                cosmetic.remove(player);
                cosmetics.remove(cosmetic);
            }

//            player.sendMessage(C.C + "You are no longer using the cosmetic " + C.V + cosmeticType.getPrettyName() + C.C + ".");
        }
    }

    public boolean hasActiveCosmetic(Player player, CosmeticType cosmeticType) {
        if (!activeCosmetics.containsKey(player.getUniqueId())) return false;
        for (Cosmetic cosmetic : activeCosmetics.get(player.getUniqueId())) {
            if (cosmetic.getType().equals(cosmeticType))
                return true;
        }
        return false;
    }

//    public boolean hasActiveCosmetic()

    public List<CosmeticType> getCosmeticsForRank(Rank rank) {
        return cosmetics.stream().filter(cosmetic -> (cosmetic.getPriceBucks() == 0
                && cosmetic.getPriceUC() == 0) || rank.isHigherThanOrEqualTo(cosmetic.getRank()))
                .map(Cosmetic::getType)
                .collect(Collectors.toList());
    }

    public MenuCosmetics getMenuCosmetics() {
        return menuCosmetics;
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() == UpdateType.SEC) {

            for (Map.Entry<UUID, Set<Cosmetic>> uuidSetEntry : activeCosmetics.entrySet()) {
                Player player = UtilPlayer.get(uuidSetEntry.getKey());
                if (player == null) continue;
                uuidSetEntry.getValue().forEach(cosmetic -> cosmetic.tick(player.getLocation()));
            }
        }
    }

}
