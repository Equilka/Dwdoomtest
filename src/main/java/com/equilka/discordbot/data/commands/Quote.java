package com.equilka.discordbot.data.commands;

import com.equilka.discordbot.data.BotData;

public class Quote implements BotData {
    private String id;
    private String messageId;
    private String quote;
    private String author;
    private String date;

    public Quote(String id, String messageId, String quote, String author, String date) {
        this.id = id;
        this.messageId = messageId;
        this.quote = quote;
        this.author = author;
        this.date = date;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }
}
