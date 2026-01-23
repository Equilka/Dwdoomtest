package com.equilka.discordbot.events.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class EmbedManager {

    public static void sendSimpleEmbed(SlashCommandInteractionEvent event,
                                       @Nullable String title, @Nullable String description, @Nullable String footer, @Nullable Color color) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (title != null)
            embedBuilder.setTitle(title);
        if (description != null)
            embedBuilder.setDescription(description);
        if (footer != null)
            embedBuilder.setFooter(footer);
        if (color != null)
            embedBuilder.setColor(color);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    public static void sendErrorEmbed(SlashCommandInteractionEvent event, String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(description)
                .setColor(new Color(218, 34, 34));

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public static MessageEmbed createSimpleEmbed(@Nullable String title, @Nullable String description, @Nullable String footer, @Nullable Color color) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (title != null)
            embedBuilder.setTitle(title);
        if (description != null)
            embedBuilder.setDescription(description);
        if (footer != null)
            embedBuilder.setFooter(footer);
        if (color != null)
            embedBuilder.setColor(color);

        return embedBuilder.build();
    }
}