package com.equilka.discordbot.events.voice;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.events.commands.SmartSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Locale;

public class JoinCommand extends SmartSlashCommand {
    private final Bot bot;
    public JoinCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "join";
        this.help = lr.getTranslatable(lang, "command.join.help");

        this.bot = bot;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        bot.getVoiceManager().JoinChannel(event);
    }
}

