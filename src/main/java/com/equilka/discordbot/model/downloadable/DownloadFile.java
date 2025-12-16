package com.equilka.discordbot.model.downloadable;

import com.equilka.discordbot.data.DataRepository;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadFile extends Thread {
    private final String url;
    private final ProgressListener progressListener;
    private final String mode;
    private String output;

    private static final Pattern PROGRESS_PATTERN = Pattern.compile(
            "\\[download\\]\\s+(\\d+\\.\\d+)%.*?at\\s+([^\\s]+).*?ETA\\s+([^\\s]+)"
    );

    private static final Pattern DEST_PATTERN = Pattern.compile(
            "Destination:\\s+(.+)$"
    );

    private static final Pattern MERGE_PATTERN = Pattern.compile(
            "Merging formats into \"(.+)\""
    );

    private static final Pattern ALREADY_PATTERN = Pattern.compile(
            "\\[download] (.+?) has already been downloaded"
    );

    public DownloadFile(String url, String mode, ProgressListener progressListener) {
        this.url = url;
        this.mode = mode;
        this.progressListener = progressListener;
    }

    public interface ProgressListener {
        void onProgress(float percent, String speed, String eta, String line);
    }

    @Override
    public void run() {
        File folder = new File(System.getProperty("user.dir"), "downloads");
        if (!folder.exists()) folder.mkdirs();

        ProcessBuilder ytdlp = new ProcessBuilder(
                "yt-dlp",
                "--cookies", new DataRepository("cookies.txt").getPath(),
                "-P", folder.getPath(),
                "-f", mode,
                url
        );
        ytdlp.redirectErrorStream(true);
        Process process;
        try {
            process = ytdlp.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            long last = 0;
            while ((line = reader.readLine()) != null) {
                Matcher dm = DEST_PATTERN.matcher(line);
                if (dm.find()) {
                    output = dm.group(1);
                }

                Matcher mm = MERGE_PATTERN.matcher(line);
                if (mm.find()) {
                    output = mm.group(1);
                }

                Matcher am = ALREADY_PATTERN.matcher(line);
                if (am.find()) {
                    output = am.group(1);
                    return;
                }

                long now = System.currentTimeMillis();
                if (now - last >= 1500) {
                    printProgress(line);
                    last = now;
                }

                if (line.startsWith("ERROR:") || line.startsWith("WARNING:") || line.contains("Got error:")) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printProgress(String line) {
        Matcher pm = PROGRESS_PATTERN.matcher(line);
        if (pm.find() && progressListener != null) {
            float percent = Float.parseFloat(pm.group(1));
            String speed = pm.group(2);
            String eta = pm.group(3);

            progressListener.onProgress(percent, speed, eta, line);
        }
    }

    public String getOutput() {
        return output;
    }
}
