package com.equilka.discordbot.commands.general.notify;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.data.commands.Notification;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotifyCommand extends SmartSlashCommand {
    public NotifyCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "notify";
        this.help = lr.getTranslatable(lang, "command.notify.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", lr.getTranslatable(lang, "command.notify.option.user"), true));
        options.add(new OptionData(OptionType.STRING, "message", lr.getTranslatable(lang, "command.notify.option.message"), false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        dataRepository.load();

        User subscriber = event.getUser();
        User target = event.getOption("user").getAsUser();
        String message = null;
        if (event.getOption("message") != null)
            message = event.getOption("message").getAsString();
        Guild guild = event.getGuild();
        Notification notification = new Notification(subscriber, target, guild, message);

        dataRepository.add(notification);
        dataRepository.save();

        event.reply(lr.getTranslatable(lang, "command.notify.reply.message")).setEphemeral(true).queue();
    }
}


