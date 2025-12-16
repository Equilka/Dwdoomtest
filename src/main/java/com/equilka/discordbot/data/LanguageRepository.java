package com.equilka.discordbot.data;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

public class LanguageRepository {
    private final Map<Locale, ResourceBundle> bundles = new HashMap<>();

    public LanguageRepository() {
        loadBondles();
    }

    private void loadBondles() {
        ClassLoader cl = LanguageRepository.class.getClassLoader();
        URL lang = cl.getResource("lang");
        if (lang == null) {
            System.err.println("Error reading translations. Check if resources/lang exists");
            return;
        }

        File langFolder;
        File[] locFiles;
        try {
            langFolder = new File(lang.toURI());
            locFiles = langFolder.listFiles();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (locFiles == null) {
            System.err.println("Error reading translations. Check if any localisation file exists in resources/lang");
            return;
        }

        for (File locFile : locFiles) {
            loadLocFile(new Locale(locFile.getName().replace(".properties", "")));
        }
    }

    private void loadLocFile(Locale locLocale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lang." + locLocale.getDisplayName(), locLocale);
            bundles.put(locLocale, bundle);
            System.out.println("Localisation file loaded: " + locLocale);
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
