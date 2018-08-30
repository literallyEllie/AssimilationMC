package net.assimilationmc.assibungee.announce;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnnounceManager extends Module {

    private File file;
    private List<String> messages;

    private int announceTask;
    private int nextMessage;

    /**
     * A manger to dispatch literal announcements to the network.
     *
     * @param assiBungee the plugin instance.
     */
    public AnnounceManager(AssiBungee assiBungee) {
        super(assiBungee, "Announce Manager");
    }

    @Override
    protected void start() {
        this.messages = Lists.newArrayList();
        this.file = new File(getPlugin().getDataFolder(), "announcements.yml");
        this.announceTask = -1;

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) throw new IOException();
            } catch (IOException e) {
                getPlugin().getLogger().severe("Failed to create " + file.getName() + "!");
                e.printStackTrace();
            }
            return;
        }

        getPlugin().getBungeeCommandManager().registerCommand(new CmdGAnnounce(getPlugin()));

        reload();
    }

    @Override
    protected void end() {
        messages.clear();
    }

    /**
     * Reload from file and start the loop again.
     */
    public void reload() {
        try {
            Configuration configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
            this.messages = configuration.getStringList("announcements");
        } catch (IOException e) {
            getPlugin().getLogger().warning("Failed to load announcements file!");
            e.printStackTrace();
        }

        if (announceTask != -1) {
            getPlugin().getProxy().getScheduler().cancel(announceTask);
        }

        this.nextMessage = 0;
        announceTask = getPlugin().getProxy().getScheduler().schedule(getPlugin(), () -> {

            if (nextMessage > messages.size() - 1)
                nextMessage = 0;

            getPlugin().getRedisManager().sendPubSubMessage("ANNOUNCEMENTS", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                    getPlugin().getServerData().getId(), "HELLO", new String[]{ChatColor.translateAlternateColorCodes('&', messages.get(
                    nextMessage))}));

            nextMessage++;
        }, 1, 2, TimeUnit.MINUTES).getId();

    }

}
