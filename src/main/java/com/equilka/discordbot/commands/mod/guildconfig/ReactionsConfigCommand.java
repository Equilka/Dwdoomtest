package com.equilka.discordbot.commands.mod.guildconfig;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.data.commands.GuildStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReactionsConfigCommand extends SmartSlashCommand {
    public ReactionsConfigCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "rconfig";
        this.help = lr.getTranslatable(lang, "command.rconfig.help");
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "action", lr.getTranslatable(lang, "command.rconfig.option.action"), true)
                .addChoice("add", "add")
                .addChoice("remove", "remove")
                .addChoice("list", "list"));
        options.add(new OptionData(OptionType.STRING, "type", lr.getTranslatable(lang, "command.rconfig.option.type"), true)
                .addChoice("upvote", "upvote")
                .addChoice("downvote", "downvote"));
        options.add(new OptionData(OptionType.STRING, "emoji", lr.getTranslatable(lang, "command.rconfig.option.emoji"), false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        dataRepository.load();

        String action = event.getOption("action").getAsString();
        String type = event.getOption("type").getAsString();
        String emoji = event.getOption("emoji") == null
                ? null
                : event.getOption("emoji").getAsString();

        if (!action.equals("list") && emoji == null) {
            event.reply(lr.getTranslatable(lang, "command.rconfig.error.emoji_not_specified")).setEphemeral(true).queue();
            return;
        }
        if (action.equals("list")) {
            event.replyEmbeds(listConfig(event, type)).setEphemeral(true).queue();
            return;
        }

        GuildStats stats = dataRepository.getAll(GuildStats.class).stream().filter(g -> g.getId().equals(event.getGuild().getId())).findFirst().get();

        if (type.equals("upvote"))
            stats = changeUpVote(event, stats, action, emoji);
        else
            stats = changeDownVote(event, stats, action, emoji);

        if (stats.getUpVoteReactions().isEmpty() || stats.getDownVoteReactions().isEmpty()) {
            return;
        }

        dataRepository.removeById(event.getGuild().getId(), GuildStats.class);
        dataRepository.add(stats);

        dataRepository.save();
        event.reply(lr.getTranslatable(lang, "command.rconfig.response.done")).setEphemeral(true).queue();
    }

    private List<String> newListEdit(SlashCommandInteractionEvent event, List<String> newList, String action, String emoji) {
        boolean statsContainsEmoji = newList.contains(emoji);
        if (!statsContainsEmoji && action.equals("add"))
            newList.add(emoji);
        else if (statsContainsEmoji && action.equals("remove"))
            newList.remove(emoji);
        else if (!statsContainsEmoji && action.equals("remove")) {
            event.reply(lr.getTranslatable(lang, "command.rconfig.error.no_emoji")).queue();
            return null;
        } else if (statsContainsEmoji && action.equals("add")) {
            event.reply(lr.getTranslatable(lang, "command.rconfig.error.yes_emoji")).queue();
            return null;
        }

        return newList;
    }

    private GuildStats changeUpVote(SlashCommandInteractionEvent event, GuildStats stats, String action, String emoji) {
        stats.setUpVoteReactions(newListEdit(event, stats.getUpVoteReactions(), action, emoji));
        return stats;
    }

    private GuildStats changeDownVote(SlashCommandInteractionEvent event, GuildStats stats, String action, String emoji) {
        stats.setDownVoteReactions(newListEdit(event, stats.getDownVoteReactions(), action, emoji));
        return stats;
    }

    private MessageEmbed listConfig(SlashCommandInteractionEvent event, String type) {
        GuildStats stats = dataRepository.getAll(GuildStats.class).stream().filter(g -> g.getId().equals(event.getGuild().getId())).findFirst().get();
        String emojis;
        if (type.equals("upvote"))
            emojis = String.join("\n", stats.getUpVoteReactions());
        else
            emojis = String.join("\n", stats.getDownVoteReactions());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(106, 12, 105))
                .setTitle(lr.getTranslatable(lang, "command.rconfig.response.list") + " " + type, null)
                .addField("", emojis, false);

        return embedBuilder.build();
    }

}
