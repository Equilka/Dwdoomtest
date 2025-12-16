package com.equilka.discordbot.data.downloadable.play;

import com.equilka.discordbot.data.BotData;
import com.equilka.discordbot.data.downloadable.AudioData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlayListData implements BotData {
    String id;
    boolean playing;
    String creator;
    LocalDateTime timeCreated;
    AudioData currentAudio;
    List<String> audioList = new ArrayList<>();

    public PlayListData(String creator, String channelId) {
        this.creator = creator;
        this.timeCreated = LocalDateTime.now();

        this.id = channelId;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getCreator() {
        return creator;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public AudioData getCurrentAudio() {
        return currentAudio;
    }

    public void setCurrentAudio(AudioData currentAudio) {
        this.currentAudio = currentAudio;
    }

    public List<String> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<String> audioList) {
        this.audioList = audioList;
    }
}
