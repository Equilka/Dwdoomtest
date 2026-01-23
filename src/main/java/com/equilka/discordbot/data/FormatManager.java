package com.equilka.discordbot.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FormatManager extends Thread {
    private final String url;
    private String output;
    private boolean isFormats = false;

    public FormatManager(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        ProcessBuilder ytdlp = new ProcessBuilder(
                "yt-dlp",
                "--cookies", new DataRepository("cookies.txt").getPath(),
                "--list-formats",
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
            List<String> formatsTable = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                printFormats(line, formatsTable);

                if (line.startsWith("ERROR:") || line.startsWith("WARNING:") || line.contains("Got error:")) {
                    System.out.println(line);
                }
            }

            output = String.join("\n", formatsTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printFormats(String line, List<String> formatsTable) {
        if (line.startsWith("[info] Available formats for"))
            isFormats = true;

        if (!isFormats)
            return;

        formatsTable.add(line);
    }

    public String getOutput() {
        return output;
    }
}
