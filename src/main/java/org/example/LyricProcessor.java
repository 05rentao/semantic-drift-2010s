package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.*;
import java.util.*;

public class LyricProcessor {

    public static void main(String[] args) {
        try {
            // 1. Load stopwords
            LyricCleaner.loadStopwords("data/stopwords.txt");

            // 2. Open lyrics.csv (both reading and then writing)
            File inputFile = new File("data/lyrics.csv");
            File tempFile  = new File("data/lyrics_temp.csv"); // temporary file

            try (CSVReader reader = new CSVReader(new FileReader(inputFile));
                 CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {

                List<String[]> allRows = reader.readAll();
                boolean isHeader = true;

                for (String[] row : allRows) {
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
                    String[] cleanedRow = new String[] { year, artist, song, cleanedLyrics };
                    writer.writeNext(cleanedRow);
                }
            }

            // Replace original file
            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
                System.out.println("✅ Cleaned lyrics written back into lyrics.csv");
            } else {
                System.out.println("❌ Could not replace original file!");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
