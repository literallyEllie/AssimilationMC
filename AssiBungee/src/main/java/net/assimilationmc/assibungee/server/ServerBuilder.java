package net.assimilationmc.assibungee.server;

import com.google.common.base.Preconditions;
import net.assimilationmc.assibungee.rank.Rank;

public class ServerBuilder {

    private final String id;

    private boolean local = true;
    private String address = "localhost";
    private int port;

    private boolean restricted = false;
    private Rank requireRank = Rank.PLAYER;

    private boolean dev;

    /**
     * Build a server!
     *
     * @param id The server id, should be unique.
     */
    public ServerBuilder(String id) {
        this.id = id;
    }

    /**
     * @return the server ID.
     */
    public String getId() {
        return id;
    }

    /**
     * @return is the server local?
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * @return the address of the server.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address of a server if it is not going to be local.
     *
     * @param address the address of the server, where the players should be directed to. Remember to set the port too!
     * @return this.
     */
    public ServerBuilder setAddress(String address) {
        this.address = address;
        local = address == null || address.equalsIgnoreCase("localhost");
        return this;
    }

    /**
     * @return the port of the server.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port of the server.
     *
     * @param port the port of the server.
     * @return this.
     */
    public ServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * @return if the server is restricted or not
     * I.e, requires a rank higher than {@link Rank#PLAYER} to do so.
     */
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * @return the required rank to join.
     */
    public Rank getRequireRank() {
        return requireRank;
    }

    /**
     * Set the required rank to join a server.
     *
     * @param requireRank the rank required to join.
     * @return this.
     */
    public ServerBuilder setRequireRank(Rank requireRank) {
        this.requireRank = requireRank;
        if (requireRank == null)
            requireRank = Rank.PLAYER;
        restricted = !requireRank.isDefault();
        return this;
    }

    /**
     * @return if the server is a development server or not.
     */
    public boolean isDev() {
        return dev;
    }

    /**
     * Set the server to a development environment.
     *
     * @param dev If it is a development server or not.
     * @return this.
     */
    public ServerBuilder setDev(boolean dev) {
        this.dev = dev;
        return this;
    }

    /**
     * @return the built {@link AssiServer} request.
     * @throws NullPointerException if there is a key bit of server information missing.
     */
    public AssiServer build() throws NullPointerException {
        Preconditions.checkNotNull(address, "Address cannot be null!");
        Preconditions.checkArgument(port < 1023 || port > 10000, "Port out of range (1024 - 10000)!");
        return new AssiServer(id, address, port, requireRank, dev);
    }

}
