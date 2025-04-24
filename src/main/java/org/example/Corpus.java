package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a corpus of documents.
 * It will create an inverted index for these documents.
 * @author swapneel
 *
 */
public class Corpus {
	
	/**
	 * An arraylist of all documents in the corpus.
	 */
	private ArrayList<Document> documents;
	
	/**
	 * The inverted index. 
	 * It will map a term to a set of documents that contain that term.
	 */
	private HashMap<String, Set<Document>> invertedIndex;
	
	/**
	 * The constructor - it takes in an arraylist of documents.
	 * It will generate the inverted index based on the documents.
	 * @param documents the list of documents
	 */
	public Corpus(ArrayList<Document> documents) {
		this.documents = documents;
		invertedIndex = new HashMap<String, Set<Document>>();
		
		createInvertedIndex();
	}

	public static void printTermIncidenceMatrix(Corpus corpus, ArrayList<String> selectedTerms) {
		System.out.println("\nTerm-Incidence Matrix:");
		ArrayList<Document> docs = corpus.getDocuments();

		// Print header
		System.out.print("Term\\Doc\t");
		for (Document doc : docs) {
			System.out.print(doc.toString() + "\t");
		}
		System.out.println();

		for (String term : selectedTerms) {
			System.out.print(term + "\t\t");
			for (Document doc : docs) {
				boolean present = doc.getTermList().contains(term);
				System.out.print((present ? "1" : "0") + "\t\t");
			}
			System.out.println();
		}
	}


	/**
	 * This method will create an inverted index.
	 */
	private void createInvertedIndex() {
		System.out.println("Creating the inverted index");
		
		for (Document document : documents) {
			Set<String> terms = document.getTermList();
			
			for (String term : terms) {
				if (invertedIndex.containsKey(term)) {
					Set<Document> list = invertedIndex.get(term);
					list.add(document);
				} else {
					Set<Document> list = new TreeSet<Document>();
					list.add(document);
					invertedIndex.put(term, list);
				}
			}
		}
	}

	public static void printTfIdfVectors(Corpus corpus, ArrayList<String> terms) {
		ArrayList<Document> docs = corpus.getDocuments();

		System.out.println("\nTF-IDF Vectors (in order): " + terms);

		for (Document doc : docs) {
			System.out.print(doc + ": [");
			for (int i = 0; i < terms.size(); i++) {
				String term = terms.get(i);
				double tf = doc.getTermFrequency(term); // raw count
				double idf = corpus.getInverseDocumentFrequency(term);
				double tfIdf = tf * idf;

				System.out.print(String.format("%.3f", tfIdf));
				if (i < terms.size() - 1) System.out.print(", ");
			}
			System.out.println("]");
		}
	}

	public double[] getTfIdfVector(Document doc, ArrayList<String> selectedTerms) {
		double[] vector = new double[selectedTerms.size()];

		for (int i = 0; i < selectedTerms.size(); i++) {
			String term = selectedTerms.get(i);
			double tf = doc.getTermFrequency(term); // raw frequency
			double idf = getInverseDocumentFrequency(term); // uses your Corpus method
			vector[i] = tf * idf;
		}

		return vector;
	}


	public static double cosineSimilarity(Double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vector lengths must match");

		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;

		for (int i = 0; i < vec1.length; i++) {
			dotProduct += vec1[i] * vec2[i];
			magnitude1 += vec1[i] * vec1[i];
			magnitude2 += vec2[i] * vec2[i];
		}
//		System.out.println("DotProduct = " + dotProduct);
//		System.out.println("Magnitude1 = " + magnitude1);
//		System.out.println("Magnitude2 = " + magnitude2);
//		System.out.println((Math.sqrt(magnitude1)));
//		System.out.println((Math.sqrt(magnitude2)));

		if (magnitude1 == 0 || magnitude2 == 0) return 0.0; // to avoid division by zero
		return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));

	}

	/**
	 * This method returns the idf for a given term.
	 * @param term a term in a document
	 * @return the idf for the term
	 */
	public double getInverseDocumentFrequency(String term) {
		if (invertedIndex.containsKey(term)) {
			double size = documents.size();
			Set<Document> list = invertedIndex.get(term);
			double documentFrequency = list.size();
			
			return Math.log10(size / documentFrequency);
		} else {
			return 0;
		}
	}

	/**
	 * @return the documents
	 */
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	/**
	 * @return the invertedIndex
	 */
	public HashMap<String, Set<Document>> getInvertedIndex() {
		return invertedIndex;
	}
}