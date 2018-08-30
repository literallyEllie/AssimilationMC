package net.assimilationmc.ellie.assicore.api;

import net.assimilationmc.ellie.assicore.util.JsonUtil;
import net.assimilationmc.ellie.assicore.util.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Ellie on 19/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiPlayer implements CommandSender {

    private int id;
    private Player base;
    private String name;
    private UUID uuid;
    private String last_ip;
    private long last_seen;
    private ArrayList<String> previous_names = new ArrayList<>();

    private String perm_rank;

    private int coins;

    public AssiPlayer(){
    }

    public AssiPlayer(Player base) {
        this.base = base;
        this.name = base.getName();
        this.uuid = base.getUniqueId();
        this.last_ip = "null";
        this.last_seen = System.currentTimeMillis();
        this.previous_names = new ArrayList<>();
        this.perm_rank = "User";
        this.coins = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean hasPermission(String permission) {
        return permission.isEmpty() || base.hasPermission(permission);
    }

    public void sendMessage(BaseComponent... baseComponent) {
        base.spigot().sendMessage(baseComponent);
    }

    public Player getBase() {
        return base;
    }

    public void setBase(Player base) {
        this.base = base;
    }

    public String getIP() {
        return last_ip;
    }

    public void setLast_ip(String ip) {
        this.last_ip = ip;
    }

    public long getLastSeen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public ArrayList<String> getPreviousNames() {
        return previous_names;
    }

    public void setPrevious_names(String previousNames) {
        this.previous_names = JsonUtil.from_gson(previousNames);
    }

    public String getPermissionsRank() {
        return perm_rank;
    }

    public void setPerm_rank(String permissionsRank) {
        this.perm_rank = permissionsRank;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean isOnline(){
        return base != null;
    }

    @Override
    public void recalculatePermissions() {
        base.recalculatePermissions();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return base.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return base.addAttachment(plugin, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return base.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return base.addAttachment(plugin, s, b, i);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return base.isPermissionSet(permission);
    }

    @Override
    public boolean isPermissionSet(String s) {
        return base.isPermissionSet(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return base.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return base.isOp();
    }

    @Override
    public void setOp(boolean b) {
        base.setOp(b);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return base.getEffectivePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        base.removeAttachment(permissionAttachment);
    }

    @Override
    public Server getServer() {
        return base.getServer();
    }

    @Override
    public void sendMessage(String s) {
        base.sendMessage(Util.color(s));
    }

    @Override
    public void sendMessage(String[] strings) {
        base.sendMessage(strings);
    }

    public void debug(){
        System.out.println("base "+base);
        System.out.println("name "+name);
        System.out.println("uuid "+ uuid);
        System.out.println("lastip "+ last_ip);
        System.out.println("previousnames "+ previous_names);
        System.out.println("last seen "+ last_seen);
        System.out.println("perm rank "+ perm_rank);
        System.out.println("coins "+ coins);

    }

}

