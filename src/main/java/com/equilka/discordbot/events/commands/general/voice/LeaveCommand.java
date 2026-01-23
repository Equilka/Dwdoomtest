package com.equilka.discordbot.events.voice;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.events.commands.SmartSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Locale;

public class LeaveCommand extends SmartSlashCommand {
    private final Bot bot;
    public LeaveCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "leave";
        this.help = lr.getTranslatable(lang, "command.leave.help");

        this.bot = bot;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        bot.getVoiceManager().LeaveChannel(event);
    }
}

