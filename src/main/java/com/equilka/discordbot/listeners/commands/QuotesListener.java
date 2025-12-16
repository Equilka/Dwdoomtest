package com.equilka.discordbot.listeners.commands;

import com.equilka.discordbot.data.commands.GuildQuotes;
import com.equilka.discordbot.data.commands.Quote;
import com.equilka.discordbot.data.commands.GuildStats;
import com.equilka.discordbot.data.DataRepository;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QuotesListener extends ListenerAdapter {
    private final DataRepository dataRepository;
    private final DataRepository guildData;

    public QuotesListener() {
        this.dataRepository = new DataRepository("quotes.json");
        this.guildData = new DataRepository("guildsData.json");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        createQuote(message);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        Message message = event.getMessage();
        createQuote(message);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        String messageId = event.getMessageId();
        dataRepository.load();

        Optional<GuildQuotes> quotesOptional = dataRepository.getAll(GuildQuotes.class)
                .stream().filter(g -> g.getId().equals(event.getGuild().getId())).findFirst();
        List<Quote> quotes = quotesOptional.map(GuildQuotes::getQuotes).orElseGet(ArrayList::new);

        Optional<Quote> quoteToDelete = quotes.stream().filter(q -> q.getMessageId().equals(messageId)).findFirst();
        quoteToDelete.ifPresent(quotes::remove);
        quotesOptional.ifPresent(guildQuotes -> dataRepository.removeById(guildQuotes.getId(), GuildQuotes.class));

        GuildQuotes newGuildQuotes = new GuildQuotes(event.getGuild().getId(), quotes);

        dataRepository.add(newGuildQuotes);
        dataRepository.save();
    }

    private boolean isValid(Message message) {
        guildData.load();
        GuildStats guild = guildData.getAll(GuildStats.class).stream()
                .filter(g -> Objects.equals(g.getId(), message.getGuild().getId())).findFirst().get();
        String channel = guild.getQuotesChannelId();
        if (channel == null)
            return false;

        boolean isQuotesChannel = message.getChannel().getId().equals(channel);
        boolean authorIsBot = message.getAuthor().isBot();
        boolean isThread = message.getChannelType().isThread();
        boolean isEphemeral = message.isEphemeral();
        boolean isVoiceMessage = message.isVoiceMessage();
        boolean isWebhookMessage = message.isWebhookMessage();

        return isQuotesChannel && !authorIsBot && !isThread && !isEphemeral && !isVoiceMessage && !isWebhookMessage;
    }

    private void createQuote(Message message) {
        if (isValid(message)) {
            dataRepository.load();

            Optional<GuildQuotes> quotesOptional = dataRepository.getAll(GuildQuotes.class)
                    .stream().filter(g -> g.getId().equals(message.getGuild().getId())).findFirst();
            List<Quote> quotes = quotesOptional.map(GuildQuotes::getQuotes).orElseGet(ArrayList::new);

            String id = Integer.toString(quotes.size() + 1);
            String messageId = message.getId();
            String messageText = message.getContentDisplay();
            String autor = message.getAuthor().getName();
            String date = message.getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

            for (String line : messageText.split("\n")) {
                if (line.startsWith("©")) {
                    autor = line.replace("©", "");
                    messageText = messageText.replace(line, "");
                }
            }

            Optional<Quote> oldQuote = quotes.stream().filter(q -> q.getMessageId().equals(messageId)).findFirst();
            if (oldQuote.isPresent()) {
                id = oldQuote.get().getId();
                quotes.remove(oldQuote.get());
            }
            quotesOptional.ifPresent(guildQuotes -> dataRepository.removeById(guildQuotes.getId(), GuildQuotes.class));

            quotes.add(new Quote(id, messageId, messageText, autor, date));
            GuildQuotes newGuildQuotes = new GuildQuotes(message.getGuild().getId(), quotes);

            dataRepository.add(newGuildQuotes);
            dataRepository.save();
        }
    }
}