package com.equilka.discordbot.commands;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.data.DataRepository;
import com.equilka.discordbot.data.LanguageRepository;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Locale;

public class SmartSlashCommand extends SlashCommand {
    protected DataRepository dataRepository;
    protected final LanguageRepository lr;
    protected final Locale lang;
    protected final Bot bot;

    public SmartSlashCommand(Bot bot, Locale lang) {
        this.lr = bot.getLanguageRepository();
        this.lang = lang;
        this.bot = bot;
        this.dataRepository = new DataRepository("guildsData.json");
    }

    protected void execute(SlashCommandInteractionEvent slashCommandEvent) { }
    @Override
    protected void execute(SlashCommandEvent event) { }
    public boolean isGuildCommand() {
        return true;
    }
}
