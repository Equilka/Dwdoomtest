package com.equilka.discordbot.data.commands;

import com.equilka.discordbot.data.BotData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification implements BotData {
    private final String id;
    private final Long subscriber;
    private final Long target;
    private final Long guild;
    private final String message;
    private final String time;

    public Notification(User subscriber, User target, Guild guild, @Nullable String message) {
        this.subscriber = subscriber.getIdLong();
        this.target = target.getIdLong();
        this.guild = guild.getIdLong();
        this.message = message;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("DD hh:mm"));

        this.id = subscriber.getName() + "_" + target.getName() + "_" + time;
    }

    public String getId() { return id; }

    public Long getSubscriber() {
        return subscriber;
    }

    public Long getTarget() {
        return target;
    }

    public Long getGuild() {
        return guild;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
