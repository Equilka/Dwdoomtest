package com.equilka.discordbot;

import com.equilka.discordbot.commands.CommandsManager;
import com.equilka.discordbot.data.LanguageRepository;
import com.equilka.discordbot.data.config.BotConfig;
import com.equilka.discordbot.model.voice.VoiceManager;
import net.dv8tion.jda.api.JDA;

import java.util.Locale;

public class Bot {
    private BotConfig botConfig;
    private JDA jda;
    private LanguageRepository languageRepository;
    private VoiceManager voiceManager;
    private CommandsManager commandsManager;

    public Bot() {
        this.botConfig = new BotConfig();
        this.languageRepository = new LanguageRepository();
        this.voiceManager = new VoiceManager(languageRepository, Locale.forLanguageTag(botConfig.getLangCode()));
        this.commandsManager = new CommandsManager(this);
    }

    public JDA getJda() {
        return jda;
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    public BotConfig getConfig() { return botConfig; }

    public VoiceManager getVoiceManager() { return voiceManager; }

    public LanguageRepository getLanguageRepository() { return languageRepository; }

    public CommandsManager getCommandsManager() { return commandsManager; }
}
