package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
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

                System.out.println("✅ Columns with 1-letter words removed and saved to vectors_filtered.csv");
            } else {
                System.out.println("❌ CSV file is empty or has no header.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//        try (CSVReader reader = new CSVReader(new FileReader(inputFile))) {
//            // Read the first row (header)
//            String[] header = reader.readNext();
//
//            if (header != null) {
//                // Filter words that are 2 letters or less
//                List<String> shortWords = new ArrayList<>();
//                for (String column : header) {
//                    List<String> words = Arrays.asList(column.split("\\s+"));
//                    for (String word : words) {
//                        if (word.length() <= 2) {
//                            shortWords.add(word);
//                        }
//                    }
//                }
//
//                // Output the list of short words
//                System.out.println("Words with 2 letters or less: " + shortWords);
//            } else {
//                System.out.println("❌ CSV file is empty or has no header.");
//            }
//        } catch (Exception e) {
//            System.out.println("❌ Error: " + e.getMessage());
//            e.printStackTrace();
//        }