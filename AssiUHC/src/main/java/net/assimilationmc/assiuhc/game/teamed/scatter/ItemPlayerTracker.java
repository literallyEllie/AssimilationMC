package net.assimilationmc.assiuhc.game.teamed.scatter;

import net.assimilationmc.assicore.util.*;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ItemPlayerTracker {

    private Player owner;
    private Player target;
    private int targetIndex;
    private GameTeam team;
    private ItemBuilder display;

    public ItemPlayerTracker(Player player, GameTeam team) {
        this.owner = player;
        this.team = team;

        this.display = new ItemBuilder(Material.COMPASS).setDisplay(C.II + "No one to find.").setLore(C.C, C.C + "Use this to find your team mates",
                C.C, C.V +"Right-click" + C.C + " to iterate through them", C.C);
        owner.getInventory().addItem(display.build());
    }

    public void tick(AssiGame assiGame) {
        if (team.getPlayers().size() == 1) return;
        Player closestPlayer = null;
        double closestDistance = 1000;

        if (target != null && !assiGame.getDeathLogger().hasDied(target)) {
            closestDistance = target.getLocation().distance(owner.getLocation());
            closestPlayer = target;
        } else {

            for (int i = 0; i < team.getPlayers().size(); i++) {
                UUID uuid = team.getPlayers().get(i);
                if (uuid.equals(owner.getUniqueId())) continue;
                Player player = UtilPlayer.get(uuid);
                if (assiGame.getDeathLogger().hasDied(player)) continue;

                final double distance = player.getLocation().distance(owner.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                    targetIndex = i;
                }
            }
            target = closestPlayer;
        }

        updateItem(closestPlayer, closestDistance);
    }

    public void onRightClick(AssiGame assiGame) {
        final List<UUID> players = team.getPlayers();
        if (players.size() == 1) return;
        if (targetIndex + 1 >= players.size()) {
            D.d("reached end of list, looking for next");
            Player oldTarget = target;
            target = null;
            tick(assiGame);

            if (target != null && oldTarget != target) owner.sendMessage(GC.C + "Now tracking: " + GC.V + target.getName());
            return;
        }

        for (;;) {
            if (targetIndex + 1 >= players.size()) {
                onRightClick(assiGame);
                break;
            }

            targetIndex++;
            UUID targetUuid = players.get(targetIndex);
            if (owner.getUniqueId().equals(targetUuid)) continue;
            Player player = UtilPlayer.get(targetUuid);
            if (assiGame.getDeathLogger().hasDied(player)) continue;

            if (target != player) {
                owner.sendMessage(GC.C + "Now tracking: " + GC.V + player.getName());
                target = player;
            }

            break;
        }

    }

    public void updateItem(Player target, double distance) {
        if (target == null) {
            display.setDisplay(C.II + "No one to find.");
        } else {
            display.setDisplay(GC.V + target.getName() + C.C + " (" + C.V + UtilMath.trim((float) distance) + "m" + C.C + ")");
        }

        final int i = removeItem();
        if (i == -1) {
            return;
        }

        owner.getInventory().setItem(i, display.build());
    }

    public int removeItem() {
        int slot = -1;
        for (int i = 0; i < owner.getInventory().getSize(); i++) {
            ItemStack item = owner.getInventory().getItem(i);
            if (item != null && item.getType() == Material.COMPASS) {
                slot = i;
            }
        }

//        if (slot != -1)
//            owner.getInventory().remove(Material.COMPASS);

        return slot;
    }


}
