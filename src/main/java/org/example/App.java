package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        int startYear = 2010;
        Parser songGetter = initializeSongGetter(startYear);
        ArrayList<String>[] songUrls = getSongUrls(songGetter);

        int targetYear = 2014;
        Parser lyricGetter = initializeLyricGetter(targetYear);
        updateParserOutliers();

        HashMap<String, String> lyricsMap = getLyricsForYear(lyricGetter, songUrls[targetYear - startYear]);
        System.out.println(lyricsMap);

        File lyricsFile = new File("lyrics.csv");
        Set<String> existingKeys = readExistingKeys(lyricsFile);
        int addedCount = writeNewLyricsToCsv(lyricsFile, existingKeys, targetYear, lyricsMap);

        System.out.println("✅ Added " + addedCount + " new songs from " + targetYear + " to lyrics.csv");
    }

    private static Parser initializeSongGetter(int year) {
        return new Parser(year);
    }

    private static ArrayList<String>[] getSongUrls(Parser parser) {
        return parser.getSongs();
    }

    private static Parser initializeLyricGetter(int year) {
        Parser lyricGetter = new Parser(2010); // Initial year doesn't matter as we set it next
        lyricGetter.year = year;
        return lyricGetter;
    }

    private static void updateParserOutliers() {
        Parser.updateOutliers();
    }

    private static HashMap<String, String> getLyricsForYear(Parser parser, ArrayList<String> urls) {
        return parser.getLyricsForYear(urls);
    }

    private static Set<String> readExistingKeys(File file) {
        Set<String> existingKeys = new HashSet<>();
        if (file.exists()) {
            Pattern csvPattern = Pattern.compile("^(\\d+),\"([^\"]+)\",\"([^\"]+)\",");
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // skip header
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher m = csvPattern.matcher(line);
                    if (m.find()) {
                        String yr = m.group(1);
                        String song = m.group(2);
                        String artist = m.group(3);
                        existingKeys.add(yr + "/" + song + "/" + artist);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return existingKeys;
    }

    private static int writeNewLyricsToCsv(File file, Set<String> existingKeys, int year, HashMap<String, String> lyricsMap) {
        boolean fileExists = file.exists();
        int added = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.write("year,song,artist,lyrics");
                writer.newLine();
            }

            for (Map.Entry<String, String> entry : lyricsMap.entrySet()) {
                String[] parts = entry.getKey().split("/");
                if (parts.length != 2) continue;

                String song = parts[0];
                String artist = parts[1];
                String lyrics = entry.getValue().replace("\"", "\"\"");

                String key = year + "/" + song + "/" + artist;
                if (existingKeys.contains(key)) {
                    continue;
                }

                String row = String.format("%d,\"%s\",\"%s\",\"%s\"",
                        year, song, artist, lyrics);
                writer.write(row);
                writer.newLine();

                existingKeys.add(key);
                added++;
            }
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
        return added;
    }
}