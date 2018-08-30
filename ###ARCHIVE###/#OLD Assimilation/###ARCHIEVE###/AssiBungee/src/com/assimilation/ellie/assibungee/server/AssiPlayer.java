package com.assimilation.ellie.assibungee.server;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by Ellie on 19/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiPlayer implements CommandSender {

    private ProxiedPlayer base;
    private String name;
    private final UUID uuid;
    private String ip;
    private long lastSeen;
    private ArrayList<String> previousNames;

    private String permissionsRank;
    private int playRank;

    private int coins;

    public AssiPlayer(ProxiedPlayer base) {
        this.base = base;
        this.name = base.getName();
        this.uuid = base.getUniqueId();
        this.ip = "null";
        this.lastSeen = System.currentTimeMillis();
        this.previousNames = new ArrayList<>();
        this.permissionsRank = "default";
        this.playRank = 0;
        this.coins = 0;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean hasPermission(String permission) {
        return permission.isEmpty() || base.hasPermission(permission);
    }

    public void sendMessage(BaseComponent... baseComponent) {
        base.sendMessage(baseComponent);
    }

    public ProxiedPlayer getBase() {
        return base;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public ArrayList<String> getPreviousNames() {
        return previousNames;
    }

    public void setPreviousNames(ArrayList<String> previousNames) {
        this.previousNames = previousNames;
    }

    public String getPermissionsRank() {
        return permissionsRank;
    }

    public void setPermissionsRank(String permissionsRank) {
        this.permissionsRank = permissionsRank;
    }

    public int getPlayRank() {
        return playRank;
    }

    public void setPlayRank(int playRank) {
        this.playRank = playRank;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public Collection<String> getGroups() {
        return base.getGroups();
    }

    @Override
    public void addGroups(String... strings) {
        base.addGroups(strings);
    }

    @Override
    public void removeGroups(String... strings) {
        base.removeGroups(strings);
    }

    @Override
    public Collection<String> getPermissions() {
        return base.getPermissions();
    }

    @Override
    public void setPermission(String s, boolean b) {
        base.setPermission(s, b);
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        base.sendMessage(baseComponent);
    }

    @Override
    public void sendMessage(String s) {
        base.sendMessage(new TextComponent(s));
    }


    @Override
    public void sendMessages(String... strings) {
        base.sendMessages(strings);
    }
}

