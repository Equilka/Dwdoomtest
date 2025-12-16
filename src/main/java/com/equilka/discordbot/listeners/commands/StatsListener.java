package com.equilka.discordbot.listeners.commands;

import com.equilka.discordbot.data.DataRepository;
import com.equilka.discordbot.data.commands.GuildStats;
import com.equilka.discordbot.data.commands.MemberStats;
import com.equilka.discordbot.model.guild.GuildDataUpdater;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatsListener extends ListenerAdapter {

    public StatsListener() { }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        DataRepository dataRepository = new DataRepository("guildsData.json");
        dataRepository.load();

        GuildStats stats = dataRepository.getObject(event.getGuild().getId(), GuildStats.class);
        if (stats == null) return;

        String memberId = event.getMember().getId();
        MemberStats memberStats = stats.getStats().stream()
                .filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);
        if (memberStats == null) return;

        memberStats.setMessages(memberStats.getMessages() + 1);
        stats.setMessages(stats.getMessages() + 1);

        dataRepository.save();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        DataRepository dataRepository = new DataRepository("guildsData.json");
        dataRepository.load();

        GuildStats stats = dataRepository.getObject(event.getGuild().getId(), GuildStats.class);
        if (stats == null) return;

        List<String> upVote = stats.getUpVoteReactions();
        List<String> downVote = stats.getDownVoteReactions();

        int credit = 0;
        if (upVote.contains(event.getEmoji().getName()))
            credit++;
        else if (downVote.contains(event.getEmoji().getName()))
            credit--;
        if (credit == 0) return;


        String authorId = event.getMessageAuthorId();
        MemberStats memberStats = stats.getStats().stream()
                .filter(m -> m.getId().equals(authorId)).findFirst().orElse(null);
        if (memberStats == null) return;

        memberStats.setMessages(memberStats.getMessages() + 1);
        stats.setMessages(stats.getMessages() + 1);

        dataRepository.save();
    }
}
