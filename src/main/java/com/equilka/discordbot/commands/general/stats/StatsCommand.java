package com.equilka.discordbot.commands.general.stats;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.data.commands.GuildStats;
import com.equilka.discordbot.data.commands.MemberStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StatsCommand extends SmartSlashCommand {
    private final JDA jda;

    public StatsCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.jda = bot.getJda();
        this.name = "stats";
        this.help = lr.getTranslatable(lang, "command.stats.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "type", lr.getTranslatable(lang, "command.stats.option.type"), false)
                .addChoice("join", "join")
                .addChoice("messages", "messages")
                .addChoice("voice", "voice")
                .addChoice("credit", "credit"));
        options.add(new OptionData(OptionType.STRING, "page", lr.getTranslatable(lang, "command.stats.option.page"), false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        dataRepository.load();
        GuildStats guildStats = dataRepository.getObject(event.getGuild().getId(), GuildStats.class);
        List<MemberStats> memberStats = guildStats.getStats();

        String type = "messages";
        int page = 1;
        if (event.getOption("type") != null)
            type = event.getOption("type").getAsString();
        if (event.getOption("page") != null)
            page = event.getOption("page").getAsInt();

        Comparator<MemberStats> comparator = getComparator(type);
        if (type.equals("join"))
            comparator = comparator.reversed();
        memberStats.sort(comparator);

        int size = 7;
        int from = (page - 1) * size;

        List<List<MemberStats>> membersForPage = new ArrayList<>();
        for (int i = 0; i < memberStats.size(); i += size) {
            membersForPage.add(memberStats.subList(i, Math.min(i + size, memberStats.size())));
        }

//        if (page < memberStats.size())
//            event.replyEmbeds(createErrorEmbed(page))
//                    .addActionRow(
//                            Button.primary("prev", "◀"),
//                            Button.primary("page", page + " / " + membersForPage.size()).asDisabled(),
//                            Button.primary("next", "▶")
//                    ).queue();

        Guild guild = event.getGuild();
        event.replyEmbeds(createEmbed(membersForPage.get(page - 1), guild, type, guildStats, event.getUser().getId(), from + 1))
                .addActionRow(
                        Button.primary("prev", "◀"),
                        Button.primary("page", page + " / " + membersForPage.size()).asDisabled(),
                        Button.primary("next", "▶")
                ).queue();
    }

    private void loadPage() {

    }

    private MessageEmbed createEmbed(List<MemberStats> membersForPage, Guild guild, String type, GuildStats guildStats, String id, int numberOfMember) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(184, 113, 6))
                .setTitle(lr.getTranslatable(lang, "command.stats.response.stats") + " " + type, null)
                .setDescription(lr.getTranslatable(lang, "command.stats.response.members") + " " + guildStats.getMembers() + "\n"
                        + lr.getTranslatable(lang, "command.stats.response.messages") + " " + guildStats.getMessages());

        for (MemberStats member : membersForPage) {
            int finalNumberOfMember = numberOfMember;

            guild.retrieveMemberById(member.getId()).queue(user -> {
                String nickname = user.getNickname() != null
                        ? user.getNickname()
                        : user.getUser().getName();
                embedBuilder.addField(finalNumberOfMember + ". " + nickname,
                        "\uD83D\uDCC5 " + member.getMemberSince(), true);
                embedBuilder.addField(lr.getTranslatable(lang, "command.stats.response.stats"),
                        "✉️ " + member.getMessages() + " 🕒 " + member.getVoiceTime() + "\n"
                                + "⭐ " + member.getCredit(), true);
                embedBuilder.addBlankField(true);
            });
            numberOfMember++;
        }

        embedBuilder.setColor(new Color(184, 113, 6));
        jda.retrieveUserById(id).queue(user ->
                embedBuilder.setFooter(user.getName(), user.getAvatarUrl()));
        return embedBuilder.build();
    }

    private MessageEmbed createErrorEmbed(int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(184, 113, 6))
                .setTitle(lr.getTranslatable(lang, "command.stats.error.page_not_found.1") + " " + page
                        + " " + lr.getTranslatable(lang, "command.stats.error.page_not_found.2"), null);
        return embedBuilder.build();
    }

    @NotNull
    private static Comparator<MemberStats> getComparator(String type) {
        Comparator<MemberStats> comparator = (o1, o2) -> o2.getMessages().compareTo(o1.getMessages());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (type.equals("join"))
            comparator = (o1, o2) ->
                    LocalDateTime.parse(o2.getMemberSince(), dateFormatter).compareTo(LocalDateTime.parse(o1.getMemberSince(), dateFormatter));
        else if (type.equals("voice"))
            comparator = (o1, o2) ->
                    LocalTime.parse(o2.getVoiceTime(), timeFormatter).compareTo(LocalTime.parse(o1.getVoiceTime(), timeFormatter));
        else if (type.equals("credit"))
            comparator = (o1, o2) -> o2.getCredit().compareTo(o1.getCredit());

        return comparator;
    }


}
