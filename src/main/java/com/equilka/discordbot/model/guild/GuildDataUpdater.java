package com.equilka.discordbot.model.guild;

import com.equilka.discordbot.data.commands.GuildStats;
import com.equilka.discordbot.data.commands.MemberStats;
import com.equilka.discordbot.data.DataRepository;
import com.google.gson.internal.PreJava9DateFormatProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class GuildDataUpdater {
    private final JDA jda;
    private final DataRepository dataRepository;
    private final CountDownLatch blocker;

    public GuildDataUpdater(JDA jda, CountDownLatch blocker) {
        this.jda = jda;
        this.dataRepository = new DataRepository("guildsData.json");
        this.blocker = blocker;
    }

    public void run() {
        dataRepository.load();
        List<Guild> guilds = jda.getGuilds();

        for (Guild guild : guilds) {
            UpdateGuildData.load(dataRepository, guild);
        }
        dataRepository.save();

        blocker.countDown();
    }

    private static class UpdateGuildData extends Thread {
        protected static void load(DataRepository dataRepository, Guild guild) {
            logProgress("Getting " + guild.getId());
            List<TextChannel> channels = guild.getTextChannels();
            List<Member> members = guild.getMembers();
            List<Message> messages = new ArrayList<>();

            GuildStats newGuildStat;
            if (dataRepository.getAll(GuildStats.class).isEmpty()
                    || dataRepository.getAll(GuildStats.class).stream().noneMatch(s -> s.getId().equals(guild.getId()))) {
                newGuildStat = new GuildStats(guild.getId(), members.size(), 0, new ArrayList<>());
                logProgress("Old data of " + guild.getId() + " loaded");
            }
            else {
                newGuildStat = dataRepository.getAll(GuildStats.class).stream().filter(s -> s.getId().equals(guild.getId())).findFirst().get();
                logProgress("New data of " + guild.getId() + " created");
            }


            List<String> upVote = newGuildStat.getUpVoteReactions();
            List<String> downVote = newGuildStat.getDownVoteReactions();

            logProgress("Loading messages of " + guild.getId());
            List<MemberStats> memberStats = new ArrayList<>();
            for (TextChannel channel : channels)
                messages.addAll(getAllMessages(channel));

            logProgress("Loading members of " + guild.getId());
            for (Member member : members) {
                String id = member.getId();
                String time = member.getTimeJoined().format(DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm"));

                Long messagesCount = messages.stream()
                        .filter(m -> m.getMember() != null && id.equals(m.getMember().getId()))
                        .count();

                Long credit = messages.stream()
                        .filter(m -> m.getMember() != null && id.equals(m.getMember().getId()))
                        .flatMap(m -> m.getReactions().stream())
                        .filter(r -> upVote.contains(r.getEmoji().getName()))
                        .count();

                Long minusCredit = messages.stream()
                        .filter(m -> m.getMember() != null && id.equals(m.getMember().getId()))
                        .flatMap(m -> m.getReactions().stream())
                        .filter(r -> downVote.contains(r.getEmoji().getName()))
                        .count();

                memberStats.add(new MemberStats(id, time, messagesCount, credit - minusCredit));
            }
            logProgress("Members of " + guild.getId() + " loaded");

            newGuildStat.setMessages(messages.toArray().length);
            newGuildStat.setStats(memberStats);

            if (dataRepository.getObject(guild.getId(), GuildStats.class) != null)
                dataRepository.removeById(guild.getId(), GuildStats.class);
            dataRepository.add(newGuildStat);
            logProgress("Data of " + guild.getId() + " saved");
        }

        private static List<Message> getAllMessages(TextChannel channel) {
            MessageHistory history = channel.getHistory();

            List<Message> result = new ArrayList<>();
            CompletableFuture<List<Message>> future = CompletableFuture.completedFuture(
                    history.retrievePast(100).complete()
            );

            while (true) {
                List<Message> chunk = future.join();
                if (chunk.isEmpty()) break;

                result.addAll(chunk);
                future = CompletableFuture.supplyAsync(() ->
                        history.retrievePast(100).complete()
                );
            }

            return result;
        }

        private static void logProgress(String status) {
            StringBuilder builder = new StringBuilder();
            builder.append("\r")
                    .append(status)
                    .append(" | ")
                    .append(LocalTime.now());
            System.out.println(builder);
        }
    }
}
