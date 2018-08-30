package net.assimilationmc.assicore.redis;

public class RedisServerData {

    private String host, auth;
    private int port;

    /**
     * @return the redis host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the redis host.
     *
     * @param host The host to connect to.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the redis port to connect to.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port of redis.
     *
     * @param port the redis port to connect to.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the auth code of the redis server.
     */
    String getAuth() {
        return auth;
    }

    /**
     * The effective authentication of the redis server.
     *
     * @param auth the authentication of the redis server.
     */
    void setAuth(String auth) {
        this.auth = auth;
    }

}
