package net.assimilationmc.assibungee.stafflog;

import net.assimilationmc.assibungee.util.UtilTime;
import net.md_5.bungee.api.ProxyServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class StaffLog {

    private UUID uuid;
    private long start;
    private File activityLog;
    private BufferedWriter writer;

    public StaffLog(UUID uuid, File log) {
        this.uuid = uuid;
        this.activityLog = log;

        if (!activityLog.exists()) {
            try {
                activityLog.createNewFile();
            } catch (IOException e) {
                ProxyServer.getInstance().getLogger().warning("[StaffLogger] Failed to create file for " + uuid);
                e.printStackTrace();
                return;
            }
        }

        try {
            this.writer = new BufferedWriter(new FileWriter(activityLog, true));
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().warning("[StaffLogger] Failed to open writer for " + uuid);
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (writer == null) return;

        try {
            writer.close();
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().warning("[StaffLogger] Failed to close writer for " + uuid);
            e.printStackTrace();
        } finally {
            writer = null;
        }

    }

    public void write(String classifier, String data) {
        write(classifier, data, false);
    }

    public void write(String classifier, String data, boolean date) {
        if (writer == null) return;
        try {
            writer.write("[" + UtilTime.formatNow((date ? "dd/MM " : "") + "HH:mm:ss") + "] " + (classifier != null ? classifier.toUpperCase() + ": " : "") + data);
            writer.newLine();
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().warning("[StaffLogger] Failed to write to " + uuid);
            e.printStackTrace();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public File getActivityLog() {
        return activityLog;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

}
