package net.assimilationmc.assibungee.server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GeneralServerPropertyReader extends ServerPropertyReader {

    public GeneralServerPropertyReader(File file) {
        super(file);
    }

    @Override
    public ServerData readServerData() {
        final ServerData serverData = new ServerData();

        try {
            BufferedReader reader = new
                    BufferedReader(new FileReader(getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\n", "");

                if (line.startsWith("ID")) {
                    String id = line.split("=")[1];
                    serverData.setId(id);
                    continue;
                }

                if (line.equals("DEV")) {
                    serverData.setDev();
                    continue;
                }

                if (line.equals("LOCAL")) {
                    serverData.setLocal();
                    continue;
                }

                if (line.equals("NETWORKING")) {
                    serverData.setNetworking();
                    continue;
                }

                if (line.startsWith("ENCRYPT_KEY")) {
                    serverData.setEncryptKey(line.split("=")[1]);
                }

            }

            if (serverData.isLocal() && !serverData.isDev()) {
                serverData.setDev();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverData;
    }
}
