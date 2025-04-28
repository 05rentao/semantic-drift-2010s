package org.example;

import java.io.*;
import java.util.*;

public class LyricCleaner {

    private static Set<String> stopwords;
    public static Set<String> lyricStopwords;

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
        if (stopwords == null || lyrics == null) {
            throw new IllegalStateException("Stopwords not loaded or lyrics is null");
        }

        // Normalize and clean lyrics
        lyrics = lyrics.toLowerCase();
        lyrics = lyrics.replaceAll("-", " ");
        lyrics = lyrics.replaceAll("\\b(\\w+)in'", "$1ing");
        lyrics = lyrics.replaceAll("[^a-z0-9\\s]", "");
        lyrics = lyrics.replaceAll("\\s+", " ");
        lyrics = lyrics.replaceAll("\\b(\\w+)(?:\\s+\\1\\b)+", "$1");
        lyrics = lyrics.trim();

        // Filter out stopwords
        return Arrays.stream(lyrics.split(" "))
                .filter(word -> !stopwords.contains(word))
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }

//        String lyrics = "work work work work   learn learn learn";
//        // (1) lowercase + strip punctuation
//        lyrics = lyrics.toLowerCase().replaceAll("[^a-z0-9\\s']", " ");
//        lyrics = lyrics.replaceAll("\\s+", " ").trim();
//
//        // (2) collapse repeats
//        lyrics = lyrics.replaceAll("\\b(\\w+)(?:\\s+\\1\\b)+", "$1")
//        lyrics = lyrics.replaceAll("\\b(\\w+)(?:\\s+\\1\\b)+", "$1"); // Remove repeated words
//        lyrics = lyrics.replaceAll("\\s+", " ").trim();
//
//        System.out.println(lyrics);  // -> "work learn"
}
