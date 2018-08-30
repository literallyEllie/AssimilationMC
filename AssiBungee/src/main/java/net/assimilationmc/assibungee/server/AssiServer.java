package net.assimilationmc.assibungee.server;

import net.assimilationmc.assibungee.rank.Rank;

public class AssiServer {

    private final String id;
    private String address;
    private int port;
    private Rank requiredRank;
    private boolean dev;

    public AssiServer(String id, String address, int port, Rank requiredRank, boolean dev) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.requiredRank = requiredRank;
        this.dev = dev;
    }

    public AssiServer(String id, String address, int port, boolean dev) {
        this(id, address, port, Rank.PLAYER, dev);
    }

    public AssiServer(String id, String address, int port, Rank requiredRank) {
        this(id, address, port, requiredRank, false);
    }

    public AssiServer(String id, String address, int port) {
        this(id, address, port, false);
    }

    public AssiServer(String id, int port, Rank requiredRank, boolean dev) {
        this(id, "localhost", port, requiredRank, dev);
    }

    public AssiServer(String id, int port, Rank requiredRank) {
        this(id, "localhost", port, requiredRank);
    }

    public AssiServer(String id, int port) {
        this(id, "localhost", port);
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Rank getRequiredRank() {
        return requiredRank;
    }

    public void setRequiredRank(Rank requiredRank) {
        this.requiredRank = requiredRank;
    }

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }
}
