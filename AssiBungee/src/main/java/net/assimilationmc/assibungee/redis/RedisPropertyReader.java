package net.assimilationmc.assibungee.redis;

import net.assimilationmc.assibungee.server.data.ServerPropertyReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RedisPropertyReader extends ServerPropertyReader {

    /**
     * A handler which is read from the redis file.
     *
     * @param file The file to read from.
     */
    public RedisPropertyReader(File file) {
        super(file);
    }

    @Override
    public RedisServerData readRedis() {
        RedisServerData redisServerData = new RedisServerData();

        try {
            final BufferedReader reader = new BufferedReader(new FileReader(getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                final String[] args = line.split("=");
                if (args[0].equals("HOST")) {
                    redisServerData.setHost(args[1]);
                    continue;
                }

                if (args[0].equals("PORT")) {
                    redisServerData.setPort(Integer.parseInt(args[1]));
                    continue;
                }

                if (args[0].equals("AUTH")) {
                    redisServerData.setAuth(args[1]);
                }

            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return redisServerData;
    }
}
