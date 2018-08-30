package net.assimilationmc.assibungee.server.data;

public class ServerData {

    private String id;
    private boolean dev, local, networking;
    private String encryptKey;

    ServerData() {
    }

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public boolean isDev() {
        return dev;
    }

    void setDev() {
        this.dev = true;
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

    public String getEncryptKey() {
        return encryptKey;
    }

    void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

}
