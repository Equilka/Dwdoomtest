package com.equilka.discordbot.data.downloadable;


import java.io.File;
import java.time.LocalDate;

public class VideoData extends Downloadable {
    public VideoData(String title, String link, File file, String mode) {
        super(title, link, file, mode);
    }
}
