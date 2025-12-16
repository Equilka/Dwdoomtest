package com.equilka.discordbot.model.downloadable;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.data.LanguageRepository;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Time;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataCleaner extends ListenerAdapter {
    private final ScheduledExecutorService scheduler;
    protected final LanguageRepository lr;
    protected final Locale lang;

    public DataCleaner(Bot bot, Locale lang) {
        this.lr = bot.getLanguageRepository();
        this.lang = lang;
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onReady(ReadyEvent event) {
        scheduler.scheduleAtFixedRate(() ->
        {
            try {
                File downloadDir = new File(System.getProperty("user.dir"), "downloads");
                if (!downloadDir.exists())
                    return;
                File[] files = downloadDir.listFiles();
                int deleted = 0;

                assert files != null;
                for (File file : files) {
                    BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                    long fileAge = System.currentTimeMillis() - attributes.lastAccessTime().toMillis();
                    if (fileAge > (14 * 24 * 60 * 60 * 1000)) {
                        file.delete();
                        deleted++;
                    }
                }

                System.out.println(lr.getTranslatable(lang, "system.data_cleaner.message") + " " + deleted + "/" + files.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, 2, TimeUnit.DAYS);
    }
}
