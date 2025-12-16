package com.equilka.discordbot.commands.general.random;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomCommand extends SmartSlashCommand {
    public RandomCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "random";
        this.help = lr.getTranslatable(lang, "command.random.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "first", lr.getTranslatable(lang, "command.random.option.first"), true));
        options.add(new OptionData(OptionType.STRING, "second", lr.getTranslatable(lang, "command.random.option.second"), false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        int first = event.getOption("first").getAsInt() + 1;
        int second = 0;
        if (event.getOption("second") != null)
            second = event.getOption("second").getAsInt();
        Random rnd = new Random();
        int result;

        if (second == 0) {
            result = rnd.nextInt(first);
            event.replyEmbeds(createEmbeded(event.getJDA(), event.getUser().getId(), result)).queue();
            return;
        }
        result = rnd.nextInt(first, second);
        event.replyEmbeds(createEmbeded(event.getJDA(), event.getUser().getId(), result)).queue();
    }

    private MessageEmbed createEmbeded(JDA jda, String userId, int result) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(127, 30, 6))
                .setTitle(lr.getTranslatable(lang, "command.random.response") + " " + result);

        jda.retrieveUserById(userId).queue(user ->
                embedBuilder.setFooter(user.getName(), user.getAvatarUrl()));

        return embedBuilder.build();
    }

    @Override
    public boolean isGuildCommand() {
        return false;
    }
}
