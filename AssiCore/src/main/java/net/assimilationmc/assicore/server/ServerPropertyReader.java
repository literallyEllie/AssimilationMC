package net.assimilationmc.assicore.server;

import net.assimilationmc.assicore.redis.RedisServerData;
import net.assimilationmc.assicore.web.WebServerData;

import java.io.File;
import java.util.Map;

public abstract class ServerPropertyReader {

    private final File file;

    /**
     * @param file Typically a file that has no extension and contains just text
     */
    public ServerPropertyReader(File file) {
        this.file = file;
    }

    /**
     * Should be a method that processes a server request file and returns it back
     *
     * @return A {@link ServerData} instance containing request for the server to begin
     * @throws UnsupportedOperationException If its not implemented
     */
    public ServerData readServerData() {
        throw new UnsupportedOperationException("Server property read not supported!");
    }

    /**
     * Should be a method that processes the SQL file credentials and returns it back
     *
     * @return A map containing the keys and values of sql properties
     * @throws UnsupportedOperationException If its not implemented
     */
    public Map<String, String> readSQL() {
        throw new UnsupportedOperationException("SQL property read not supported!");
    }

    /**
     * Should be a method that processes the Redis credentials and settings file and returns it back
     *
     * @return An instance of {@link RedisServerData} for info to connect
     * @throws UnsupportedOperationException If its not implemented
     */
    public RedisServerData readRedis() {
        throw new UnsupportedOperationException("Redis property read not supported!");
    }

    /**
     * Should be a method that processes Web API address and token and returns it back
     *
     * @return the instance of {@link WebServerData} containing request to connect to.
     */
    public WebServerData readWebData() {
        throw new UnsupportedOperationException("Web Data read not supported!");
    }

    /**
     * Get the file to read from
     *
     * @return The file to read from
     */
    public File getFile() {
        return file;
    }

}
