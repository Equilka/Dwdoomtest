package com.equilka.discordbot.listeners.commands;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.data.LanguageRepository;
import com.equilka.discordbot.data.commands.Notification;
import com.equilka.discordbot.data.DataRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationsListener extends ListenerAdapter {
    private final DataRepository dataRepository;
    private final ScheduledExecutorService schedule;
    protected final LanguageRepository lr;
    protected final Locale lang;

    public NotificationsListener(Bot bot, Locale lang) {
        this.lr = bot.getLanguageRepository();
        this.lang = lang;
        this.dataRepository = new DataRepository("notifications.json");
        this.schedule = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();

        schedule.scheduleAtFixedRate(() ->
        {
            try {
                dataRepository.load();
                for (Notification notification : dataRepository.getAll(Notification.class)) {
                    Guild guild = jda.getGuildById(notification.getGuild());
                    String date = notification.getTime();

                    Member target = guild.retrieveMemberById(notification.getTarget()).complete();
                    Member subscriber = guild.retrieveMemberById(notification.getSubscriber()).complete();


                    if (target.getOnlineStatus().equals(OnlineStatus.ONLINE)) {
                        subscriber.getUser().openPrivateChannel()
                                .queue((channel) -> channel
                                        .sendMessageEmbeds(createEmbeded(jda, date, target.getId(), " " + lr.getTranslatable(lang, "pm.notification.user_online"), null)).queue());
                        if (notification.getMessage() != null)
                            target.getUser().openPrivateChannel()
                                    .queue((channel) -> channel
                                            .sendMessageEmbeds(createEmbeded(jda, date, subscriber.getId(), lr.getTranslatable(lang, "pm.notification.user_awaits"), notification.getMessage())).queue());

                        dataRepository.removeById(notification.getId(), Notification.class);
                        dataRepository.save();
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to scedule status notifications: " + e);
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private MessageEmbed createEmbeded(JDA jda, String date, String userId, String status, @Nullable String message) {
        String nickname = jda.getUserById(userId).getName();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(95, 255, 66))
                .setTitle(nickname + status);
        embedBuilder.setDescription(message);


        jda.retrieveUserById(userId).queue(user ->
                embedBuilder.setFooter(lr.getTranslatable(lang, "pm.notification.created") + " " + date, user.getAvatarUrl()));

        return embedBuilder.build();
    }
}
