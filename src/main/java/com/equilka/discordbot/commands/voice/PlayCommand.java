package com.equilka.discordbot.commands.voice;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.model.downloadable.DownloadFile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PlayCommand extends SmartSlashCommand {
    private final Bot bot;

    public PlayCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "play";
        this.help = lr.getTranslatable(lang, "command.play.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "link", lr.getTranslatable(lang, "command.play.option.link"), true));

        this.options = options;
        this.bot = bot;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue(interactionHook -> {

            String link = event.getOption("link").getAsString();
            CompletableFuture.runAsync(() -> {
                try {
                    DownloadFile thread = new DownloadFile(link, "ba",
                            (percent, speed, eta, raw) -> {
                                interactionHook.editOriginal(
                                        lr.getTranslatable(lang, "command.play.response.progress") + " " + percent + " | "
                                                + lr.getTranslatable(lang, "command.play.response.speed") + " " + speed + " | "
                                                + lr.getTranslatable(lang, "command.play.response.eta") + " " + eta).queue();
                            });
                    thread.start();
                    thread.join();

                    bot.getVoiceManager().PlayMusic(event, thread.getOutput(), interactionHook);
                } catch (Exception e) {
                    System.err.println(e);
                    e.printStackTrace();
                    interactionHook.editOriginal(lr.getTranslatable(lang, "command.play.error") + " " + e.getMessage()).queue();
                }
            });
        });
    }
}


