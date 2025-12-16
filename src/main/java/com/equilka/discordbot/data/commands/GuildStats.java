package com.equilka.discordbot.data.commands;

import com.equilka.discordbot.data.BotData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuildStats implements BotData {
    private String id;
    private int members;
    private int messages;
    private String quotesChannelId;
    private List<String> upVoteReactions = new ArrayList<>();
    private List<String> downVoteReactions = new ArrayList<>();
    private Locale guildLanguage;
    private List<MemberStats> stats;

    public GuildStats(String id, int members, int messages, List<MemberStats> stats) {
        this.id = id;
        this.members = members;
        this.messages = messages;
        this.quotesChannelId = null;
        this.upVoteReactions.add("⬆\uFE0F");
        this.downVoteReactions.add("⬇\uFE0F");
        this.guildLanguage = Locale.US;
        this.stats = stats;
    }

    public GuildStats(String id, GuildStats newStats) {
        this.id = id;
        this.members = newStats.getMembers();
        this.messages = newStats.getMessages();
        this.quotesChannelId = newStats.getQuotesChannelId();
        this.upVoteReactions = newStats.getUpVoteReactions();
        this.downVoteReactions = newStats.getDownVoteReactions();
        this.guildLanguage = newStats.getGuildLanguage();
        this.stats = newStats.getStats();
    }

    @Override
    public String getId() {
        return id;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public List<MemberStats> getStats() {
        return stats;
    }

    public void setStats(List<MemberStats> stats) {
        this.stats = stats;
    }

    public String getQuotesChannelId() {
        return quotesChannelId;
    }

    public void setQuotesChannelId(String quotesChannelId) {
        this.quotesChannelId = quotesChannelId;
    }

    public List<String> getUpVoteReactions() {
        return upVoteReactions;
    }

    public void setUpVoteReactions(List<String> upVoteReactions) {
        this.upVoteReactions = upVoteReactions;
    }

    public List<String> getDownVoteReactions() {
        return downVoteReactions;
    }

    public void setDownVoteReactions(List<String> downVoteReactions) {
        this.downVoteReactions = downVoteReactions;
    }

    public Locale getGuildLanguage() {
        return guildLanguage;
    }

    public void setGuildLanguage(Locale guildLanguage) {
        this.guildLanguage = guildLanguage;
    }
}
