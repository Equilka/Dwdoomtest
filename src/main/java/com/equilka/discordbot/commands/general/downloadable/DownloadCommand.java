package com.equilka.discordbot.commands.general.downloadable;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.model.downloadable.DownloadFile;
import com.equilka.discordbot.model.downloadable.FormatManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DownloadCommand extends SmartSlashCommand {
    public DownloadCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "download";
        this.help = lr.getTranslatable(lang, "command.download.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "link", lr.getTranslatable(lang, "command.download.option.link"), true));
        options.add(new OptionData(OptionType.STRING, "format", lr.getTranslatable(lang, "command.download.option.format"), false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue(interactionHook -> {
            dataRepository.load();

            String link = event.getOption("link").getAsString();
            String format;
            if (event.getOption("format") != null)
                format = event.getOption("format").getAsString();
            else {
                format = null;
            };

            CompletableFuture.runAsync(() -> {
                try {
                    if (format == null) {
                        FormatManager thread = new FormatManager(link);
                        thread.start();
                        thread.join();

                        interactionHook.editOriginal(thread.getOutput()).queue();
                    } else {
                        DownloadFile thread = new DownloadFile(link, format,
                                (percent, speed, eta, raw) -> {
                                    interactionHook.editOriginal(
                                            lr.getTranslatable(lang, "command.download.response.progress") + " " + percent + " | "
                                            + lr.getTranslatable(lang, "command.download.response.speed") + " " + speed + " | "
                                            + lr.getTranslatable(lang, "command.download.response.eta") + " " + eta).queue();
                                });
                        thread.start();
                        thread.join();

                        File file = new File(thread.getOutput());
                        if (file.length() <= 8000000) {
                            interactionHook.editOriginal(lr.getTranslatable(lang, "command.download.response.complete"))
                                    .setFiles(FileUpload.fromData(file)).queue();
                        } else {
                            interactionHook.editOriginal(lr.getTranslatable(lang, "command.download.response.file_too_large")).queue();
                            interactionHook.editOriginal("https://" + bot.getConfig().getDomain() + "/" + file.getName().trim()).queue();
                        }
                    }

                } catch (Exception e) {
                    System.err.println(e);
                    e.printStackTrace();
                    interactionHook.editOriginal(lr.getTranslatable(lang, "command.download.error") + " " + e.getMessage()).queue();
                }
            });
        });
    }
}


