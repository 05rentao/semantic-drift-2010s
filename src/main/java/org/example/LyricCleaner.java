package org.example;

import java.io.*;
import java.util.*;

public class LyricCleaner {

    private static Set<String> stopwords;

    public static void loadStopwords(String filepath) throws IOException {
        stopwords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.trim().toLowerCase());
            }
        }
    }

    public static String cleanLyrics(String lyrics) {
        if (stopwords == null) {
            throw new IllegalStateException("Stopwords not loaded. Call loadStopwords() first.");
        }

        lyrics = lyrics.toLowerCase();
        lyrics = lyrics.replaceAll("[^a-z0-9\\s]", "");
        lyrics = lyrics.replaceAll("\\s+", " ").trim();

        StringBuilder cleaned = new StringBuilder();
        for (String word : lyrics.split(" ")) {
            if (!stopwords.contains(word)) {
                cleaned.append(word).append(" ");
            }
        }

        return cleaned.toString().trim();
    }
}
