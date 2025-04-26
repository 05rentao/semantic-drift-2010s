package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Initialize parsers and song URLs
        Parser songGetter = new Parser(2010);
        ArrayList<String>[] songUrls = songGetter.getSongs();
        Parser lyricGetter = new Parser(2010);

        // Set target year
        lyricGetter.year = 2023;
        int year = lyricGetter.year;

        Parser.updateOutliers();

        // Fetch lyrics for the specific year
        HashMap<String, String> lyricsMap = lyricGetter.getLyricsForYear(songUrls[year - 2010]);
        System.out.println(lyricsMap);

        File csvFile = new File("lyricsRaw.csv");
        Set<String> existingKeys = readExistingKeys(csvFile);
        appendLyricsToCsv(csvFile, lyricsMap, existingKeys, year);
    }

    /**
     * Reads the existing CSV and returns a set of keys (year/artist/song) already present.
     */
    private static Set<String> readExistingKeys(File file) {
        Set<String> existingKeys = new HashSet<>();
        if (!file.exists()) {
            return existingKeys;
        }
        Pattern csvPattern = Pattern.compile("^(\\d+),\"([^\"]+)\",\"([^\"]+)\",");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // skip header
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = csvPattern.matcher(line);
                if (m.find()) {
                    String yr     = m.group(1);
                    String song   = m.group(2);
                    String artist = m.group(3);
                    existingKeys.add(yr + "/" + song + "/" + artist);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existingKeys;
    }

    /**
     * Appends new lyrics entries to the CSV, skipping duplicates.
     */
    private static void appendLyricsToCsv(File file,
                                          Map<String, String> lyricsMap,
                                          Set<String> existingKeys,
                                          int year) {
        boolean fileExists = file.exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.write("year,song,artist,lyrics");
                writer.newLine();
            }

            int added = 0;
            for (Map.Entry<String, String> entry : lyricsMap.entrySet()) {
                String[] parts = entry.getKey().split("/");
                if (parts.length != 2) continue;
                String song   = parts[0];
                String artist = parts[1];
                String lyrics = entry.getValue().replace("\"", "\"\"");

                String key = year + "/" + song + "/" + artist;
                if (existingKeys.contains(key)) {
                    continue;
                }

                String row = String.format(
                        "%d,\"%s\",\"%s\",\"%s\"",
                        year, song, artist, lyrics
                );
                writer.write(row);
                writer.newLine();

                existingKeys.add(key);
                added++;
            }

            System.out.println("✅ Added " + added + " new songs from " + year + " to lyricsRaw.csv");
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
    }
}
