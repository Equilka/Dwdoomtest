package com.equilka.discordbot.data.downloadable;

import com.equilka.discordbot.data.BotData;

import java.io.File;
import java.time.LocalDate;

public class Downloadable implements BotData {
    String id;
    String title;
    String link;
    File file;
    String quality;

    public Downloadable(String title, String link, File file, String mode) {
        this.title = title;
        this.link = link;

        this.id = title + "_" + mode;
        this.file = file;
        this.quality = mode;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public File getFile() {
        return file;
    }

    public String getQuality() {
        return quality;
    }

    public void addFile(File file, String quality) {
        this.file = file;
        this.quality = quality;
    }
}