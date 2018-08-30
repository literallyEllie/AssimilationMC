package net.assimilationmc.ellie.discord.listener;

import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.backend.JsonBotConfig;
import net.assimilationmc.ellie.discord.util.Channels;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by Ellie on 14/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class OtherListeners extends ListenerAdapter {

    private JsonBotConfig config;

    public OtherListeners(JsonBotConfig config){
        this.config = config;

    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getTextChannelById(Channels.BOT_LOGS).sendMessage("AssiDiscord has started up").queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        JDA jda = e.getJDA();
        Member member = e.getMember();

        if(config.getJoinMessage() != null&& !config.getJoinMessage().equals("null")){
            String join = config.getJoinMessage();

            join = join.replace("{taggedUser}", "<@"+member.getUser().getId()+">")
                    .replace("{guild}", e.getGuild().getName()).replace("{namedUser}", member.getEffectiveName());
                                                                // assi
            jda.getTextChannelById(e.getGuild().getIdLong() == 301711310926774283L ? Channels.PUBLIC_GENERAL : Channels.STAFF_GENERAL).sendMessage(join).queue();
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        JDA jda = e.getJDA();
        Member member = e.getMember();

        if(config.getLeaveMessage() != null&& !config.getLeaveMessage().equals("null")){
            String leave = config.getLeaveMessage();

            leave = leave.replace("{taggedUser}", "<@"+member.getUser().getId()+">")
                    .replace("{guild}", e.getGuild().getName()).replace("{namedUser}", member.getEffectiveName());
            jda.getTextChannelById(e.getGuild().getIdLong() == 301711310926774283L ? Channels.PUBLIC_GENERAL : Channels.STAFF_GENERAL).sendMessage(leave).queue();
        }

    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        AssiDiscord.getAssiDiscord().messageChannel(Channels.BOT_LOGS, "**NOTICE** Joined guild "+event.getGuild().getName()+" ("+event.getGuild().getId()+")");
    }

}