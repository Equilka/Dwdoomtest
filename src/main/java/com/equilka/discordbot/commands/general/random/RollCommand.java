package com.equilka.discordbot.commands.general.random;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.data.DataRepository;
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

public class RollCommand extends SmartSlashCommand {
    public RollCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "roll";
        this.help = lr.getTranslatable(lang, "command.roll.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "dice", lr.getTranslatable(lang, "command.roll.option.dice"), true)
                .addChoice("d2", "2")
                .addChoice("d4", "4")
                .addChoice("d6", "6")
                .addChoice("d8", "8")
                .addChoice("d10", "10")
                .addChoice("d12", "12")
                .addChoice("d14", "14")
                .addChoice("d20", "20")
                .addChoice("d24", "24")
                .addChoice("d30", "30")
                .addChoice("d100", "100"));
        options.add(new OptionData(OptionType.STRING, "number", lr.getTranslatable(lang, "command.roll.option.number"), false));

        this.options = options;
    }
    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        int dice = event.getOption("dice").getAsInt();
        int number = 1;
        if (event.getOption("number") != null)
            number = event.getOption("number").getAsInt();
        Random rnd = new Random();
        List<Integer> total = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            total.add(rnd.nextInt(1, dice + 1));
        }

        event.replyEmbeds(createEmbeded(event.getJDA(), event.getUser().getId(), total)).queue();
    }

    private MessageEmbed createEmbeded(JDA jda, String userId, List<Integer> total) {
        int rollTotal = 0;
        String dicesSum = total.stream().map(Object::toString).collect(Collectors.joining(" + "));
        for (int i : total) {
            rollTotal += i;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(127, 30, 6))
                .setTitle(lr.getTranslatable(lang, "command.roll.response") + " " + rollTotal)
                .setImage("https://i.kym-cdn.com/photos/images/original/002/606/087/73d.gif")
                .setDescription(dicesSum);


        jda.retrieveUserById(userId).queue(user ->
                embedBuilder.setFooter(user.getName(), user.getAvatarUrl()));

        return embedBuilder.build();
    }

    @Override
    public boolean isGuildCommand() {
        return false;
    }
}
