package net.assimilationmc.assicore.server;

import net.assimilationmc.assicore.rank.Rank;

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

                if (line.equals("LOBBY")) {
                    serverData.setLobby();
                    continue;
                }

                if (line.equals("UHC")) {
                    serverData.setUhc();
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

                if (line.startsWith("MIN_RANK")) {
                    String rankStr = line.split("=")[1];
                    Rank rank = Rank.fromString(rankStr);
                    if (rank == null) throw new IllegalArgumentException("Invalid rank " + rankStr + "!");
                    serverData.setRequiredRank(rank);
                }

                if (line.equals("ANALYTICS")) {
                    serverData.setAnalytics(true);
                    continue;
                }

                if (line.startsWith("ANALYTICS_TOKEN")) {
                    serverData.setAnalyticsToken(line.split("=")[1]);
                }

                if (line.startsWith("ENCRYPT_KEY")) {
                    serverData.setEncryptKey(line.split("=")[1]);
                }

            }

            if (serverData.isLocal() && !serverData.isDev()) {
                serverData.setDev();
            }

            if ((serverData.isLocal() || serverData.isDev() ||
                    serverData.getAnalyticsToken() == null || serverData.getAnalyticsToken().isEmpty())
                    && serverData.isAnalytics()) {
                serverData.setAnalytics(false);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverData;
    }
}
