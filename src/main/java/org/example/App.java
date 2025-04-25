package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        Parser songGetter = new Parser(2010);
        ArrayList<String>[] songUrls = songGetter.getSongs();
        Parser lyricGetter = new Parser(2010);

        int year = 2010;
        HashMap<String, String> lyricsMap = lyricGetter.getLyricsForYear(songUrls[year - 2010]);
        // have lyrics in hashmap format, song, format
        System.out.println(lyricsMap);

        File file = new File("lyrics.csv");
        boolean fileExists = file.exists(); // check if CSV already exists

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Only write header if file is new
            if (!fileExists) {
                writer.write("year,song,artist,lyrics");
                writer.newLine();
            }

            for (Map.Entry<String, String> entry : lyricsMap.entrySet()) {
                String[] parts = entry.getKey().split("/");
                if (parts.length != 2) continue;

                String song = parts[0];
                String artist = parts[1];
                String lyrics = entry.getValue().replace("\"", "\"\""); // escape quotes

                String row = String.format("%d,\"%s\",\"%s\",\"%s\"", year, song, artist, lyrics);
                writer.write(row);
                writer.newLine();
            }

            System.out.println("✅ Added " + lyricsMap.size() + " songs from " + year + " to lyrics.csv");

        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
    }
}
