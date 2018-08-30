package net.assimilationmc.assicore.server;

import net.assimilationmc.assicore.rank.Rank;

public class ServerData {

    private String id;
    private boolean lobby, uhc, dev, local, networking, analytics;
    private String analyticsToken;
    private Rank requiredRank;
    private String encryptKey;

    ServerData() {
    }

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public boolean isLobby() {
        return lobby;
    }

    void setLobby() {
        this.lobby = true;
    }

    public boolean isDev() {
        return dev;
    }

    void setDev() {
        this.dev = true;
    }

    public boolean isUhc() {
        return uhc;
    }

    void setUhc() {
        this.uhc = true;
    }

    public boolean isLocal() {
        return local;
    }

    void setLocal() {
        this.local = true;
    }

    public boolean isNetworking() {
        return networking;
    }

    void setNetworking() {
        this.networking = networking;
    }

    public boolean isAnalytics() {
        return analytics;
    }

    void setAnalytics(boolean state) {
        this.analytics = state;
    }

    public String getAnalyticsToken() {
        return analyticsToken;
    }

    void setAnalyticsToken(String analyticsToken) {
        this.analyticsToken = analyticsToken;
    }

    public Rank getRequiredRank() {
        return requiredRank;
    }

    public void setRequiredRank(Rank requiredRank) {
        this.requiredRank = requiredRank;
    }

    public boolean hasRequiredRank() {
        return requiredRank != null;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

}
