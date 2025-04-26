package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TextCleaner {

    private Set<String> stopWords;

    public TextCleaner(String stopWordsFilePath) {
        this.stopWords = loadStopWords(stopWordsFilePath);
    }

    private Set<String> loadStopWords(String filePath) {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error loading stop words from file: " + filePath);
            // If the file cannot be loaded, initialize with an empty set to avoid NullPointerException
            return new HashSet<>();
        }
        return words;
    }

    public String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 1. Convert to lowercase
        String lowerCaseText = text.toLowerCase();

        // 2. Remove punctuation, especially parentheses
        String withoutPunctuation = lowerCaseText.replaceAll("[^a-z\\s']", "").trim();
        // Explanation of the regex:
        // [^...] : Matches any character that is NOT inside the brackets
        // a-z   : Matches lowercase letters a through z
        // \\s   : Matches any whitespace character (space, tab, newline, etc.)
        // '     : Matches the apostrophe character (to keep contractions like "don't")

        // 3. Remove random spaces (multiple spaces replaced by a single space)
        String normalizedSpaces = withoutPunctuation.replaceAll("\\s+", " ");

        // 4. Tokenize the text into words
        String[] words = normalizedSpaces.split(" ");

        // 5. Remove stop words
        StringBuilder cleanedTextBuilder = new StringBuilder();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                cleanedTextBuilder.append(word).append(" ");
            }
        }

        // trim any trailing space and return
        return cleanedTextBuilder.toString().trim();
    }


    public static void main(String[] args) {
        String stopWordsFile = "data/stopwords.txt";
        TextCleaner cleaner = new TextCleaner(stopWordsFile);
        String inputCsvFile = "data/lyricsRaw.csv";
        String outputCsvFile = "data/lyric.csv"; // Name of the new CSV file

        try (BufferedReader reader = new BufferedReader(new FileReader(inputCsvFile));
             FileWriter writer = new FileWriter(outputCsvFile)) {

            String headerLine = reader.readLine(); // Read the header
            if (headerLine != null) {
                writer.write(headerLine + ",cleaned_lyrics\n"); // Write the header with a new column
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 3) {
                    String year = parts[0];
                    String artist = parts[1];
                    String song = parts[2];
                    String lyricsRaw = parts[3];
                    String cleanedLyrics = cleaner.cleanText(lyricsRaw);

                    // Write the original data and the cleaned lyrics to the new CSV file
                    writer.write(year + "," + artist + "," + song + "," + lyricsRaw + "," + cleanedLyrics + "\n");
                } else {
                    // Handle cases where a line might have fewer columns
                    System.err.println("Skipping line with insufficient columns: " + line);
                }
            }
            System.out.println("Cleaned lyrics written to: " + outputCsvFile);

        } catch (IOException e) {
            System.err.println("Error processing CSV files: " + e.getMessage());
        }
    }
}