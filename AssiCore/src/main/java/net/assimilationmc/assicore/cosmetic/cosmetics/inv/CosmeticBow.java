package net.assimilationmc.assicore.cosmetic.cosmetics.inv;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.cosmetic.Cosmetic;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CosmeticBow extends Cosmetic implements Listener {

    private ItemStack tpBow, tpArrow;
    private List<Player> usingCosmetic;

    public CosmeticBow(AssiPlugin plugin) {
        super(CosmeticType.BOW, "A fun way to teleport", Rank.INFERNAL);

        this.usingCosmetic = Lists.newArrayList();
        this.tpBow = new ItemBuilder(Material.BOW).setDisplay(ChatColor.LIGHT_PURPLE + "Teleport Bow").build();
        this.tpArrow = new ItemBuilder(Material.ARROW).setDisplay(ChatColor.DARK_PURPLE + "Magic Arrow").build();

        plugin.registerListener(this);

    }

    @Override
    public void apply(Player player) {
        player.closeInventory();
        player.getInventory().setItem(4, tpBow);
        player.getInventory().setItem(9, tpArrow);

        if (!usingCosmetic.contains(player))
            usingCosmetic.add(player);

    }

    @Override
    public void remove(Player player) {
        player.getInventory().remove(Material.BOW);
        player.getInventory().remove(Material.ARROW);
        usingCosmetic.remove(player);
    }

    @EventHandler
    public void onArrowShoot(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();

        if (usingCosmetic.contains(player) && event.getEntity() instanceof Arrow) {
            final Location loc = event.getEntity().getLocation();
            loc.setYaw(player.getLocation().getYaw());
            loc.setPitch(player.getLocation().getPitch());

            player.teleport(loc);
            event.getEntity().remove();
            player.getInventory().setItem(9, tpArrow);
        }
    }

}
