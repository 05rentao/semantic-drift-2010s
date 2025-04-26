package org.example;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class VSMBuilder {


    public static void main(String[] args) {
        Corpus corpus = buildCorpus("data/songs/");
        VectorSpaceModel model = new VectorSpaceModel(corpus);
        saveVectors(corpus, model, "vectors.csv");
    }

    /**
     * Reads all song .txt files under the folder, builds a Corpus and VectorSpaceModel.
     *
     * @param baseFolderPath Path to the folder containing songs organized by year
     * @return A pair [Corpus, VectorSpaceModel]
     */
    public static Corpus buildCorpus(String baseFolderPath) {
        ArrayList<Document> documents = new ArrayList<>();
        File baseFolder = new File(baseFolderPath);

        if (!baseFolder.exists() || !baseFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder path: " + baseFolderPath);
        }

        List<File> allSongFiles = listAllSongFiles(baseFolder);

        for (File file : allSongFiles) {
            // Parse year, artist, song from path
            String[] parts = file.getPath().split(File.separator.equals("\\") ? "\\\\" : File.separator);
            String year = parts[parts.length - 2];
            String filename = parts[parts.length - 1].replace(".txt", "");
            String[] nameParts = filename.split("_", 2);

            String artist = nameParts.length > 0 ? nameParts[0] : "unknown";
            String songTitle = nameParts.length > 1 ? nameParts[1] : "unknown";

            // Create Document
            Document doc = new Document(file.getPath(), year, artist, songTitle);
            documents.add(doc);
        }

        return new Corpus(documents);
    }

    /**
     * Helper: Recursively list all .txt files
     */
    private static List<File> listAllSongFiles(File folder) {
        List<File> songFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) return songFiles;

        for (File file : files) {
            if (file.isDirectory()) {
                songFiles.addAll(listAllSongFiles(file));
            } else if (file.isFile() && file.getName().endsWith(".txt")) {
                songFiles.add(file);
            }
        }
        return songFiles;
    }

    public static void saveVectors(Corpus corpus, VectorSpaceModel model, String outputCsvPath) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(outputCsvPath))) {

            // Step 1: Get full sorted list of terms
            Set<String> termSet = corpus.getInvertedIndex().keySet();
            ArrayList<String> termList = new ArrayList<>(termSet);
            Collections.sort(termList); // important for consistent column order

            // Step 2: Write CSV header
            List<String> header = new ArrayList<>(Arrays.asList("year", "artist", "song"));
            header.addAll(termList);
            writer.writeNext(header.toArray(new String[0]));

            // Step 3: For each document, write metadata + vector
            for (Document doc : corpus.getDocuments()) {
                List<String> row = new ArrayList<>();

                row.add(doc.getYear());
                row.add(doc.getArtist());
                row.add(doc.getSongTitle());

                HashMap<String, Double> tfidfVector = model.getTfIdfWeights(doc);

                for (String term : termList) {
                    double value = tfidfVector.getOrDefault(term, 0.0);
                    row.add(String.valueOf(value));
                }

                writer.writeNext(row.toArray(new String[0]));
            }

            System.out.println("✅ Saved TF-IDF vectors to " + outputCsvPath);

        } catch (IOException e) {
            System.out.println("❌ Error writing TF-IDF vectors: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
