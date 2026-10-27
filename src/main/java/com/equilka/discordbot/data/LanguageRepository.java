package com.equilka.discordbot.data;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class LanguageRepository {
    private static final String EXT = ".properties";
    private final Map<Locale, ResourceBundle> bundles = new HashMap<>();

    public LanguageRepository() {
        loadBondles();
    }

    private void loadBondles() {
        URL lang = LanguageRepository.class.getClassLoader().getResource("lang");
        if (lang == null) {
            System.err.println("Error reading translations. Check if resources/lang exists");
            return;
        }

        List<String> names = new ArrayList<>();
        try {
            URI uri = lang.toURI();
            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fs = FileSystems.newFileSystem(uri, Map.of())) {
                    collect(fs.getPath("/lang"), names);
                }
            } else {
                collect(Path.of(uri), names);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        if (names.isEmpty()) {
            System.err.println("Error reading translations. Check if any localization file exists in resources/lang");
            return;
        }

        for (String name : names) {
            loadLocFile(name);
        }
    }

    private static void collect(Path dir, List<String> names) throws IOException {
        try (Stream<Path> files = Files.list(dir)) {
            files.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(EXT))
                    .map(n -> n.substring(0, n.length() - EXT.length()))
                    .forEach(names::add);
        }
    }

    private void loadLocFile(String name) {
        try {
            Locale locale = new Locale(name);
            ResourceBundle bundle = ResourceBundle.getBundle("lang." + name, locale);
            bundles.put(locale, bundle);
            System.out.println("Localization file loaded: " + locale);
        } catch (Exception e) {
            System.out.println("Error reading translations.");
            e.printStackTrace();
        }
    }

    public String getTranslatable(Locale locale, String key) {
        try {
            Locale l = new Locale(locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase());
            ResourceBundle bundle = bundles.get(l);
            return bundle.getString(key);
        } catch(Exception e) {
            e.printStackTrace();
            return "ErrorNotFound";
        }
    }
}
