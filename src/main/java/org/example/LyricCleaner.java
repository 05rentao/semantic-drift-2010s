package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

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

    public static void remove1LetterWords() {
        File inputFile = new File("data/vectors.csv");
        File outputFile = new File("data/vectors_filtered.csv");

        try (CSVReader reader = new CSVReader(new FileReader(inputFile));
             CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {

            // Read the first row (header)
            String[] header = reader.readNext();

            if (header != null) {
                // Identify columns to keep (where words are not 1 letter long)
                List<Integer> columnsToKeep = new ArrayList<>();
                for (int i = 0; i < header.length; i++) {
                    String column = header[i];
                    List<String> words = Arrays.asList(column.split("\\s+"));
                    boolean hasOneLetterWord = words.stream().anyMatch(word -> word.length() == 1);
                    if (!hasOneLetterWord) {
                        columnsToKeep.add(i);
                    }
                }

                // Write filtered header
                List<String> filteredHeader = new ArrayList<>();
                for (int index : columnsToKeep) {
                    filteredHeader.add(header[index]);
                }
                writer.writeNext(filteredHeader.toArray(new String[0]));

                // Write filtered rows
                String[] row;
                while ((row = reader.readNext()) != null) {
                    List<String> filteredRow = new ArrayList<>();
                    for (int index : columnsToKeep) {
                        filteredRow.add(row[index]);
                    }
                    writer.writeNext(filteredRow.toArray(new String[0]));
                }

                System.out.println("removed 1-letter word columns and saved to vectors_filtered.csv");
            } else {
                System.out.println("CSV file is empty or has no header.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
