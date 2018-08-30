package net.assimilationmc.assicore.server.analytics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.server.analytics.payload.AnalyticPayload;
import net.assimilationmc.assicore.server.analytics.payload.OpeningAnalyticPayload;
import net.assimilationmc.assicore.server.analytics.payload.TimelyAnalyticPayload;
import net.assimilationmc.assicore.util.UtilSecurity;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AnalyticsManager extends Module {

    private List<String> plugins;

    private AnalyticPayload lastData;
    private int tenCounter;

    public AnalyticsManager(AssiPlugin plugin) {
        super(plugin, "Analytics Manager");
    }

    @Override
    protected void start() {
        plugins = Lists.newArrayList();
        log("Analytics monitoring enabled.");
        sampleInit();
    }

    @Override
    protected void end() {
        plugins.clear();
    }

    private void sampleInit() {
        final AnalyticPayload analyticPayload = new OpeningAnalyticPayload(UtilTime.now());
        collectPlugins();
        analyticPayload.setPlugins(plugins);
        analyticPayload.setServerData(getPlugin().getServerData());
        analyticPayload.setMaxPlayers(UtilServer.getMaxPlayers());
        analyticPayload.setExternalIp(UtilServer.getExternalIp());

        analyticPayload.setEnd(UtilTime.now());
        lastData = analyticPayload;
    }

    private void sampleHourly() {
        final AnalyticPayload analyticPayload = new TimelyAnalyticPayload(UtilTime.now());

        analyticPayload.setOnlinePlayers(UtilServer.getOnlinePlayers());
        analyticPayload.setPlayers(samplePlayersDetailed());
        analyticPayload.setExternalIp(UtilServer.getExternalIp());

        analyticPayload.setEnd(UtilTime.now());
        lastData = analyticPayload;
    }

    private void collectPlugins() {
        plugins.clear();
        plugins.addAll(Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.toList()));
    }

    private Map<String, Rank> samplePlayersDetailed() {
        Map<String, Rank> players = Maps.newHashMap();

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            final int MAX = 25;
            int i = 0;

            for (AssiPlayer player : getPlugin().getPlayerManager().getOnlinePlayers().values()) {
                if (i == MAX) return;

                players.put(player.getName(), player.getRank());

                i++;
            }

        });

        return players;
    }

    private void send() {
        if (lastData == null) return;
        String data = lastData.serialise(getPlugin().getServerData().getAnalyticsToken());

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL
                    ("http://localhost:8000/analytics/").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (getPlugin().getServerData().getEncryptKey() == null ||
                    getPlugin().getServerData().getEncryptKey().isEmpty()) {
                log(Level.WARNING, "Analytics requires an encryption key! We're safe!");
                return;
            }

            data = UtilSecurity.encrypt(data, getPlugin().getServerData().getEncryptKey());

            connection.getOutputStream().write(data.getBytes("UTF-8"));

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IllegalArgumentException("Illegal response from server " +
                        connection.getResponseCode() + " (" + connection.getResponseMessage() + ")");
            }

            connection.getOutputStream().close();
            connection.getInputStream().close();
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                log(Level.WARNING, "Can't connect to analytics servers, are they offline?");
                return;
            }
            log(Level.WARNING, "Error whilst sending request to analytics server!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            log(Level.WARNING, "Error whist sending request.");
            e.printStackTrace();
        }

        lastData = null;
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        /* debug */
        if (e.getType() == UpdateType.MIN) {
            send();
        }
        if (e.getType() == UpdateType.TEN_MIN) {
            if (++tenCounter == 6) {
                sampleHourly();
                send();
                tenCounter = 0;
                return;
            }
            tenCounter++;
        }
    }

}