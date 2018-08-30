package net.assimilationmc.assibungee.discord.command.staff;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.discord.DiscordCommandEnvironment;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.discord.command.DiscordCommand;
import net.assimilationmc.assibungee.server.balancer.ServerBalancer;
import net.dv8tion.jda.core.Permission;

public class DCmdBFP extends DiscordCommand {

    public DCmdBFP(DiscordManager discordManager) {
        super(discordManager, "bfp", "Force the balancers to recheck server status", Permission.ADMINISTRATOR, Lists.newArrayList(), "<server>");
    }

    @Override
    protected void onCommand(DiscordCommandEnvironment commandEnvironment) {
        ServerBalancer balancer = discordManager.getPlugin().getBalancerManager().getServerBalancer(commandEnvironment.getArgs()[0]);
        if (balancer == null) {
            discordManager.messageChannel(commandEnvironment.getChannel(), "Invalid balancer.");
            return;
        }

        discordManager.getPlugin().getProxy().getScheduler().runAsync(discordManager.getPlugin(), () -> {

            discordManager.getPlugin().getBalancerManager().checkOnline(balancer);
            discordManager.getPlugin().getBalancerManager().offlineCheck(balancer);

        });
        discordManager.messageChannel(commandEnvironment.getChannel(), "Async check started.");


    }

}
