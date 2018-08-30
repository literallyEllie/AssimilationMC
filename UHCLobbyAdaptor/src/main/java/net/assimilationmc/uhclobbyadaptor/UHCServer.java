package net.assimilationmc.uhclobbyadaptor;

import com.google.common.collect.Maps;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;

import java.util.Map;

public class UHCServer {

    private final String serverId;
    private String gamePhase, mapName;
    private int online, maxPlayers;
    private long warmupStart;
    private UHCGameSubType gameSubType;
    private Map<String, Object> customAttributes;

    public UHCServer(String serverId) {
        this.serverId = serverId;
        this.customAttributes = Maps.newHashMap();
        reset();
    }

    public String getServerId() {
        return serverId;
    }

    public String getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(String gamePhase) {
        this.gamePhase = gamePhase;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public long getWarmupStart() {
        return warmupStart;
    }

    public void setWarmupStart(long warmupStart) {
        this.warmupStart = warmupStart;
    }

    public UHCGameSubType getGameSubType() {
        return gameSubType;
    }

    public void setGameSubType(UHCGameSubType gameSubType) {
        this.gameSubType = gameSubType;
    }

    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public boolean hasCustom() {
        return !customAttributes.isEmpty();
    }

    public void reset() {
        this.gamePhase = "LOBBY";
        this.online = maxPlayers = 0;
        this.mapName = "unknown";
        this.warmupStart = 0;
    }

}
