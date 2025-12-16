package com.equilka.discordbot.data.config;

import io.github.cdimascio.dotenv.Dotenv;

public class BotConfig {
    private final Dotenv dotenv;
    private final String token;
    private final String domain;
    private final String ownerId;
    private String langCode;

    private final BotConfigData botConfigData;

    public BotConfig() {
        this.dotenv = Dotenv.load();
        
        this.token = dotenv.get("DISCORD_TOKEN_TEST");
        this.ownerId = dotenv.get("OWNER_ID");
        this.domain = dotenv.get("DOMAIN");
        this.langCode = dotenv.get("LANGUAGE_CODE");
        this.botConfigData = new BotConfigData();
    }

    public Dotenv getDotenv() {
        return dotenv;
    }

    public String getToken() {
        return token;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getDomain() {return domain;}

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public BotConfigData getBotConfigData() {
        return botConfigData;
    }
}
