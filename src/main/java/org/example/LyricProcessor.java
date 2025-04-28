package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.*;
import java.util.*;

public class LyricProcessor {

    public static void main(String[] args) {
        // preprocessLyrics();
        // createFilesFromLyrics();
    }

    private static void preprocessLyrics() {
        try {
            // 1. Load stopwords
            LyricCleaner.loadStopwords("data/stopwords.txt");

            // 2. Open lyrics.csv (both reading and then writing)
            File inputFile = new File("data/lyricsRaw.csv");
            File tempFile = new File("data/lyrics_temp.csv"); // temporary file
            File outputFile = new File("data/lyrics.csv"); // final output file

            try (CSVReader reader = new CSVReader(new FileReader(inputFile));
                 CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {

                List<String[]> allRows = reader.readAll();
                boolean isHeader = true;

                for (int i = 0; i < allRows.size(); i++) {
                    String[] row = allRows.get(i);
                    if (isHeader) {
                        // Copy header exactly
                        writer.writeNext(row);
                        isHeader = false;
                        continue;
                    }

                    String year = row[0];
                    String artist = row[1];
                    String song = row[2];
                    String lyrics = row[3];

                    // Clean the lyrics
                    String cleanedLyrics = LyricCleaner.cleanLyrics(lyrics);

                    // Write the cleaned row
                    String[] cleanedRow = new String[]{year, artist, song, cleanedLyrics};
                    writer.writeNext(cleanedRow);
                }
            }

            // Replace original file
            if (outputFile.delete()) {
                tempFile.renameTo(outputFile);
                System.out.println("✅ Cleaned lyrics written into lyrics.csv");
            } else {
                System.out.println("❌ Could not replace original file!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createFilesFromLyrics() {
        try {
            // Read the existing lyrics.csv
            File inputFile = new File("data/lyrics.csv");

            try (CSVReader reader = new CSVReader(new FileReader(inputFile))) {
                List<String[]> allRows = reader.readAll();
                boolean isHeader = true;

                for (String[] row : allRows) {
                    if (isHeader) {
                        isHeader = false;
                        continue; // skip header
                    }

                    String yearStr = row[0];
                    String artist = row[1];
                    String song = row[2];
                    String lyrics = row[3];

                    int year;
                    try {
                        year = Integer.parseInt(yearStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid year: " + yearStr);
                        continue;
                    }

                    // Clean filename (remove illegal characters)
                    String filename = artist + "_" + song;
                    filename = filename.replaceAll("[^a-zA-Z0-9\\-_]", "").toLowerCase();

                    // Build folder path
                    File yearFolder = new File("data/songs/" + year);
                    if (!yearFolder.exists()) {
                        yearFolder.mkdirs();
                    }

                    // Write to a new file
                    File outputFile = new File(yearFolder, filename + ".txt");

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                        writer.write(lyrics);
                    }
                }

                System.out.println("✅ Lyrics split into separate files under data/songs/");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
