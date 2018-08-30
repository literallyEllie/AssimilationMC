package net.assimilationmc.assicore.web;

public class WebServerData {

    private final String address, token;
    private final int port;

    public WebServerData(String address, int port, String token) {
        this.address = address;
        this.port = port;
        this.token = token;
    }

    String getAddress() {
        return address;
    }

    int getPort() {
        return port;
    }

    String getToken() {
        return token;
    }
}
