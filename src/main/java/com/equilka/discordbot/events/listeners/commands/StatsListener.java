package com.equilka.discordbot.events.listeners.commands;

import com.equilka.discordbot.data.DataRepository;
import com.equilka.discordbot.data.model.commands.GuildStats;
import com.equilka.discordbot.data.model.commands.MemberStats;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

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
        stats.getStats().set(stats.getStats().indexOf(memberStats), memberStats);
        stats.setMessages(stats.getMessages() + 1);

        dataRepository.revriteOrAdd(stats.getId(), GuildStats.class, stats);
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

        memberStats.setCredit(memberStats.getCredit() + 1);
        stats.getStats().set(stats.getStats().indexOf(memberStats), memberStats);

        dataRepository.revriteOrAdd(stats.getId(), GuildStats.class, stats);
    }
}
