package com.equilka.discordbot.model.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuildMusicManager {
    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final VoiceChannel channel;
    private final AudioManager manager;
    private final TrackScheduler schedule;
    private final AudioPlayerSendHandler handler;

    public GuildMusicManager(AudioPlayerManager playerManager, VoiceChannel channel) {
        this.playerManager = playerManager;
        this.player = playerManager.createPlayer();
        this.channel = channel;
        this.manager = channel.getGuild().getAudioManager();
        this.schedule = new TrackScheduler(this.player);
        this.player.addListener(this.schedule);

        this.handler = new AudioPlayerSendHandler(player);
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public VoiceChannel getChannel() {
        return channel;
    }

    public AudioManager getManager() {
        return manager;
    }

    public TrackScheduler getSchedule() {
        return schedule;
    }

    public AudioPlayerSendHandler getHandler() {
        return handler;
    }
}
