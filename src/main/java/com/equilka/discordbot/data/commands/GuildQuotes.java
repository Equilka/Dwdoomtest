package com.equilka.discordbot.data.commands;

import com.equilka.discordbot.data.BotData;

import java.util.List;

public class GuildQuotes implements BotData {
    private String id;
    private List<Quote> quotes;

    public GuildQuotes(String id, List<Quote> quotes) {
        this.id = id;
        this.quotes = quotes;
    }

    @Override
    public String getId() {
        return id;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}
