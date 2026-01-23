package com.equilka.discordbot.data.model.downloadable;


import java.io.File;

public class VideoData extends Downloadable {
    public VideoData(String title, String link, File file, String mode) {
        super(title, link, file, mode);
    }
}
