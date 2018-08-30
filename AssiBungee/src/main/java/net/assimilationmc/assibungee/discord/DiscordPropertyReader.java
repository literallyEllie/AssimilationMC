package net.assimilationmc.assibungee.discord;

import net.assimilationmc.assibungee.server.data.ServerPropertyReader;
import net.dv8tion.jda.core.OnlineStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DiscordPropertyReader extends ServerPropertyReader {

    public DiscordPropertyReader(File file) {
        super(file);
    }

    @Override
    public DiscordBotData readDiscord() {
        final DiscordBotData botData = new DiscordBotData();

        try {
            final BufferedReader reader = new BufferedReader(new FileReader(getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                final String[] args = line.split("=");

                if (args[0].equals("TOKEN")) {
                    botData.setToken(args[1]);
                    continue;
                }

                if (args[0].equals("GAME")) {
                    botData.setGame(args[1]);
                    continue;
                }

                if (args[0].equals("LISTEN")) {
                    botData.setListening(args[1]);
                    continue;
                }

                if (args[0].equals("WATCH")) {
                    botData.setWatching(args[1]);
                    continue;
                }

                if (args[0].equals("CMD_PREFIX")) {
                    botData.setCommandPrefix(args[1]);
                    continue;
                }

                if (args[0].equals("STATUS")) {
                    botData.setOnlineStatus(OnlineStatus.fromKey(args[1]));
                    continue;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return botData;
    }

}