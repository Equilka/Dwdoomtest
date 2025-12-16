package com.equilka.discordbot.data.commands;

import com.equilka.discordbot.data.BotData;

public class MemberStats implements BotData {
    private String id;
    private String memberSince;
    private Long messages;
    private String voiceTime;
    private Long credit;

    public MemberStats(String id, String memberSince, Long messages, Long credit) {
        this.id = id;
        this.memberSince = memberSince;
        this.messages = messages;
        this.voiceTime = "00:00";
        this.credit = credit;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMessages(Long messages) {this.messages = messages;}

    public Long getMessages() {
        return messages;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public Long getCredit() {
        return credit;
    }
}
