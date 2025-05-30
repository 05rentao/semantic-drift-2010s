# Semantic Drift in Pop Lyrics (2010–2024)

*An independent analysis by Ren Tao*

Using vector space models and clustering techniques on Billboard Top 10 songs from 2010 to 2024, **this project explores how pop lyrics have (or haven’t) changed over the past decade and a half, semantically, thematically, and structurally.**

## Overview

**What I wanted to find out:**
- Do songs from nearby years sound more similar?
- Can we group songs by their lyrical themes or semantic content?
- Have lyrics become more homogeneous over time?

To test these, I built a end-to-end system that scrapes song lyrics, vectorizes them with TF-IDF, and runs clustering and similarity analysis to explore trends across time.

## Methodology

### Phase I: Java (Scraping + Preprocessing)
- Wrote a custom scraper to pull Billboard rankings and fetch lyrics from AZLyrics
- Cleaned and normalized lyrics (lowercasing, punctuation stripping, removing repeated chorus lines)
- Used a TF-IDF vectorizer (adapted from a past course assignment) to build document vectors
- Output CSV files with metadata and vectorized lyrics

### Phase II: Python (Analysis + Visualization)
- Imported vectors into a Pandas DataFrame
- Reduced dimensions using Truncated SVD (100D + 2D)
- Calculated cosine similarity across all song pairs
- Ran multiple clustering methods: KMeans, Agglomerative, and OPTICS
- Visualized clusters and similarity trends over time

## Key Takeaways

- **Lyrics have gotten more similar** post-2016, possibly due to streaming-era trends
- **Clustering by meaning didn’t really work**, songs with wildly different topics still ended up in the same clusters (TF-IDF analysis limitations)
- **Most songs fall into a single dense semantic cluster** as they are structurally repetitive and lexically similar
- Certain years (like 2013 and 2018) stood out as outliers

## Limitations

- TF-IDF doesn’t truly capture meaning, it’s more about structure and word repetition
- SVD compression may lose nuance
- Only includes Top 10 songs per year, sample size limited

## Future Improvements

- Filter out “lyrical stopwords” (e.g. “ooh,” “yeah,” “ayy”) to improve signal
- Explore semantic embeddings (e.g. BERT) for richer clustering
- Expand to Top 100 and compare across genres

## Project Files

```
semantic-drift-2010s/
├── data/                        # All data used or generated
│   ├── songs/                   # Raw lyrics (.txt files) grouped by year (2010–2023)
│   ├── lyricsRaw.csv            # Unprocessed scraped lyrics
│   ├── lyrics.csv               # Cleaned lyrics (lowercase, no punctuation, etc.)
│   ├── lyrics_filtered.csv      # Cleaned lyrics with repeated phrases removed
│   ├── vectors.csv              # TF-IDF vector representations of songs
│   ├── vectors_filtered.csv     # Filtered vector set without 1-letter words (e.g. "u", "k")
│   └── stopwords.txt            # Stopwords used for filtering
│
├── src/
│   └── main/
│       └── java/
│           └── org/example/     # Java classes for preprocessing and vectorization
│               ├── Corpus.java
│               ├── Document.java
│               ├── LyricCleaner.java
│               ├── LyricFetcher.java
│               ├── LyricProcessor.java
│               ├── Main.java
│               ├── Parser.java
│               ├── URLGetter.java
│               ├── VectorSpaceModel.java
│               └── VSMBuilder.java
│
├── target/
├── .env
├── .gitignore
├── pom.xml
└── README.md

```


## Notebook

🔗 [Google Colab – Full analysis](https://colab.research.google.com/drive/13vJBq8wTMrBLFjapeaVnfeHeJkm3Ouc8?usp=sharing)

## Full Report
📄 [Full Report (PDF)](./docs/semantic_drift_2010s_report.pdf)

---

Lyrics from azlyrics.com | Data via Wikipedia | Stopwords: [NLTK list](https://gist.github.com/sebleier/554280)

