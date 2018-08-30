package net.assimilationmc.ellie.assishop.backend;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.manager.SQLManager;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assishop.AssiShop;
import net.assimilationmc.ellie.assishop.NPCShop;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.sql2o.Connection;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Ellie on 13.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ShopSQLStorage implements Listener { // welp

    private SQLManager sqlManager;
    private TreeMap<Integer, NPCShop> shops = new TreeMap<>();

    public ShopSQLStorage() {
        this.sqlManager = ModuleManager.getModuleManager().getSQLManager();

        try (Connection connection = sqlManager.getSql2o().open()) {
            connection.createQuery("CREATE TABLE IF NOT EXISTS `assimilation_npc` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "`location` LONGTEXT NOT NULL, " +
                    "`display` MEDIUMTEXT, " +
                    "`action`  MEDIUMTEXT, " +
                    "INDEX(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;").executeUpdate();

            List<NPCShop> shops = connection.createQuery("SELECT * FROM `assimilation_npc`").executeAndFetch(NPCShop.class);
            shops.forEach(shop -> {
                this.shops.put(shop.getId(), shop);
                spawnShop(shop);
            });
            shops.clear();
            connection.close();
        }
    }

    public void createShop(NPCShop shop) {
        int next = 0;
        if(shops.size() != 0) {
            next = shops.lastKey() + 1;
        }
        shop.setId(next);
        shops.put(next, shop);
        spawnShop(shop);
        sqlManager.getSql2o().open().createQuery("INSERT INTO `assimilation_npc` (location, display, action) VALUES (:loc, :d, :a)").
                addParameter("loc", shop.getLocation().toString()).addParameter("d", shop.getDisplay()).addParameter("a", shop.getAction()).executeUpdate().close();
    }

    public void delShop(int id){
        despawn(id);
        shops.remove(id);
        sqlManager.getSql2o().open().createQuery("DELETE FROM `assimilation_npc` WHERE id = :i").addParameter("i", id).executeUpdate().close();
    }

    public void update(NPCShop shop){
        despawn(shop.getId());
        shops.remove(shop.getId());
        shops.put(shop.getId(), shop);
        spawnShop(shop);
        sqlManager.getSql2o().open().createQuery("UPDATE `assimilation_npc` SET location = :loc, display = :d, action = :a WHERE id = :i").
                addParameter("loc", shop.getLocation().toString()).addParameter("d", shop.getDisplay()).addParameter("a", shop.getAction()).addParameter("i", shop.getId()).executeUpdate().close();
    }

    public void finish() {
        for (NPCShop npcShop : shops.values()) {
            despawn(npcShop.getId());
        }
        shops.clear();
    }

    public TreeMap<Integer, NPCShop> getShops() {
        return shops;
    }

    public NPCShop getShop(int id){
        return shops.get(id);
    }

    private void spawnShop(NPCShop shop) {
        Bukkit.getScheduler().runTask(AssiShop.getAssiShop(), () -> {
            Villager villager = Bukkit.getWorld(shop.getLocation().getWorld()).spawn(shop.getLocation().toLocation(), Villager.class);
            villager.setCanPickupItems(false);
            villager.setCustomNameVisible(true);
            villager.setAgeLock(true);
            villager.setAdult();
            villager.setCustomName(Util.color(shop.getDisplay()));
            villager.setRemoveWhenFarAway(false);
            villager.setProfession(Villager.Profession.FARMER);
            villager.setMetadata("npc-shop", new FixedMetadataValue(AssiShop.getAssiShop(), shop.getId()));

            Entity nmsEn = ((CraftEntity) villager).getHandle();
            NBTTagCompound compound = new NBTTagCompound();
            nmsEn.c(compound);
            compound.setByte("NoAI", (byte) 1);
            nmsEn.f(compound);
            shop.setEntity(villager);
        });

    }

    private void despawn(int id){
        NPCShop shop = getShop(id);
        if(shop != null && shop.getEntity() != null){
            shop.getEntity().remove();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        org.bukkit.entity.Entity clicked = e.getRightClicked();

        if(clicked instanceof Villager) {
            Villager v = (Villager) clicked;
            if(v.hasMetadata("npc-shop")) {
                e.setCancelled(true);
                handleInteract(player, v);
            }
        }
    }


    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Villager){
            Villager villager = (Villager) e.getEntity();

            if(villager.hasMetadata("npc-shop")) {
                e.setCancelled(true);
                if(e.getDamager() instanceof Player) {
                    Player player = (Player) e.getDamager();
                    handleInteract(player, villager);
                }
            }

        }
    }

    private void handleInteract(Player player, Villager villager){

        for (MetadataValue metadataValue : villager.getMetadata("npc-shop")) {
            if(metadataValue.getOwningPlugin().equals(AssiShop.getAssiShop())){
                final int id = metadataValue.asInt();
                NPCShop shop = getShop(id);
                if(shop == null){
                    Util.mWARN(player, "Shop is null.");
                    return;
                }

                final String action = shop.getAction();

                if(action.split(":")[0].equalsIgnoreCase("msg")){
                    player.sendMessage(Util.color(action.substring("msg:".length())));
                    return;
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), shops.get(id).getAction());

            }
        }

    }

}
