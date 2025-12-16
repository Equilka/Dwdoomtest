package com.equilka.discordbot.commands.general.quotes;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.EmbedManager;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.data.DataRepository;
import com.equilka.discordbot.data.commands.GuildQuotes;
import com.equilka.discordbot.data.commands.Quote;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class QuoteCommand extends SmartSlashCommand {
    public QuoteCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "quote";
        this.help = lr.getTranslatable(lang, "command.quote.help");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "id", lr.getTranslatable(lang, "command.quote.id"), false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        dataRepository.load();

        String quoteId;
        if (event.getOption("id") != null)
            quoteId = event.getOption("id").getAsString();
        else
            quoteId = null;

        Optional<GuildQuotes> guildOptional = dataRepository.getAll(GuildQuotes.class)
                .stream().filter(g -> g.getId().equals(event.getGuild().getId())).findFirst();
        if (guildOptional.isEmpty()) {
            EmbedManager.sendErrorEmbed(event, "command.quote.error.server_not_found");
            return;
        }

        List<Quote> quotes = guildOptional.get().getQuotes();
        if (quotes.isEmpty()) {
            EmbedManager.sendErrorEmbed(event, "command.quote.error.no_quotations");
            return;
        }

        if (Objects.equals(quoteId, "all")) {
            DataRepository guildData = new DataRepository(guildOptional.get().getId() + ".json");
            guildData.load();
            try {
                event.replyFiles(FileUpload.fromData(createAllQuotes(guildOptional.get(), guildData))).queue();
                guildData.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        Quote quote;
        Random rnd = new Random();
        quote = quoteId == null
                ? quotes.get(rnd.nextInt(1, quotes.size()))
                : quotes.stream().filter(q -> q.getId().equals(quoteId)).findFirst().orElse(null);

        if (quote == null) {
            EmbedManager.sendErrorEmbed(event, lr.getTranslatable(lang, "command.quote.error.quotation_not_found"));
            return;
        }

        String title = quote.getQuote();
        String description = quote.getAuthor() + " (" + quote.getDate() + ")";
        String footer = "id: " + quote.getId();
        Color color = new Color(105, 6, 127);
        EmbedManager.sendSimpleEmbed(event, title, description, footer, color);
    }

    private File createAllQuotes(GuildQuotes quotes, DataRepository data) throws IOException {
        data.add(quotes);
        data.save();

        return data.getFile();
    }
}
