package com.equilka.discordbot;

import com.equilka.discordbot.data.DataRepository;
import com.equilka.discordbot.data.config.BotConfigData;
import com.equilka.discordbot.data.config.BotConfig;
import com.equilka.discordbot.listeners.commands.NotificationsListener;
import com.equilka.discordbot.listeners.commands.QuotesListener;
import com.equilka.discordbot.listeners.commands.StatsListener;
import com.equilka.discordbot.model.downloadable.DataCleaner;
import com.equilka.discordbot.model.guild.GuildDataUpdater;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class Dwdoomtest {
    public static void main(String[] arguments)
            throws IllegalArgumentException, LoginException, RateLimitedException {
        try {
            Bot bot = new Bot();
            startBot(bot);
        } catch (Exception e) {
            System.out.println("Failed to run a bot: " + e);
            e.printStackTrace();
        }
    }


    private static void startBot(Bot bot)
            throws InterruptedException {
        BotConfig botConfig = bot.getConfig();
        BotConfigData botConfigData = botConfig.getBotConfigData();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.addSlashCommands()
                .setOwnerId(botConfig.getOwnerId());

        JDA jda = JDABuilder.create(botConfig.getToken(), botConfigData.getIntents()).setStatus(botConfigData.getStatus())
                .setMemberCachePolicy(botConfigData.getCachePolicy())
                .setChunkingFilter(botConfigData.getChunkingFilter())
                .enableCache(botConfigData.getCacheFlag())
                .addEventListeners(
                        bot.getCommandsManager(),
                        new NotificationsListener(bot, Locale.forLanguageTag(bot.getConfig().getLangCode())),
                        new StatsListener(),
                        new QuotesListener(),

                        new DataCleaner(bot, Locale.forLanguageTag(bot.getConfig().getLangCode())))
                .build();
        bot.setJda(jda);
        jda.awaitReady();
        
        System.out.println("Loading guilds data");
        CountDownLatch blocker = new CountDownLatch(1);
        GuildDataUpdater updater = new GuildDataUpdater(jda, blocker);
        updater.run();
        blocker.await();
        System.out.println("All guilds data loaded");

        jda.awaitReady();
    }
}
