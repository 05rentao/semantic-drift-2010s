package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

import static java.util.Collections.replaceAll;


public class Parser {
    URLGetter url;
    private String content;
    Matcher m;
    Pattern p;
    public int year = 2010;
    static final String azlyrics = "https://www.azlyrics.com/lyrics/";
    // https://www.azlyrics.com/lyrics/metroboomin/creepin.html
    static final String wiki = "https://en.wikipedia.org/wiki/Billboard_Year-End_Hot_100_singles_of_";
    // https://en.wikipedia.org/wiki/Billboard_Year-End_Hot_100_singles_of_2023

    /**
     * Init for finding parser object finding song titles from wiki
     * @param year to start at
     */
    Parser(int year) {
        String url = wiki + year;
        this.url = new URLGetter(url);
        updateURL();
    }

    /**
     * init to find lyrics
     * @param song to parse
     */
    Parser(String song) {
        getLyrics(song);
    }

//    /**
//     * generic parser
//     * @param url to parse
//     */
//    Parser(String url) {
//        this.url = new URLGetter(url);
//        updateURL();
//    }

    public void updateURL() {
        this.url.printStatusCode();

        ArrayList<String> page = this.url.getContents();
        StringBuilder contentBuilder = new StringBuilder();
        for (String line : page) {
            contentBuilder.append(line);
        }
        this.content = contentBuilder.toString();
    }

    public void nextYear() {
        year++;
        String url = wiki + year;
        this.url = new URLGetter(url);
        updateURL();
    }

    public void getLyrics(String song) {
        String url = azlyrics + song + ".html";
        this.url = new URLGetter(url);
        updateURL();
    }

    public void newExpression(String expression) {
        p = Pattern.compile(expression);
        m = p.matcher(this.content);
    }

    public ArrayList<String> groups(int num) throws NoSuchElementException {
        ArrayList<String> groups = new ArrayList<>();

        if (num < 0) {
            throw new IllegalArgumentException("groups: number must be a positive integer");
        }
        if (num == 0) {
            while (m.find()) {
                groups.add(m.group());
            }
        } else {

            while (m.find()) {
                groups.add(m.group(num));
            }
        }
        if (groups.isEmpty()) {
            throw new NoSuchElementException();
        }
        return groups;
    }

    public ArrayList<String> groups2() {
        ArrayList<String> groups = new ArrayList<>();
        while (m.find()) {
            groups.add(m.group());
        }
        return groups;
    }

    public ArrayList<String>[] getSongs() {
        // scrap wikipedia for song names + artist names
        // store then in to an arraylist of size 14,
        // starting at 0, is year 2010, 1 -> 2011, 2 -> 2012 etc.
        @SuppressWarnings("unchecked")
        ArrayList<String>[] songsByYear = (ArrayList<String>[]) new ArrayList[15];

        while (year < 2025) {

            newExpression("<td>\"<a href=[^>]*>([^<]+)<.*?\\s*<td><a href=[^>]*>([^<]+)<");
            ArrayList<String> songsThisYear = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                if (m.find()) {
                    String currSong = m.group(1).toLowerCase().replaceAll("[^a-z0-9]", "");
                    // System.out.println(m.group(1));
                    String currArtist = m.group(2).toLowerCase().replaceAll("[^a-z0-9]", "");
                    String urlComponent = currArtist + "/" + currSong;
                    songsThisYear.add(urlComponent);
                } else {
                    System.out.println("Error: no more matches for year " + year +
                            " only " + songsThisYear.size() + " matches found");
                }
            }

            songsByYear[year - 2010] = songsThisYear;
            if (year < 2024) {
                nextYear();
            } else {
                break;
            }

        }
        return songsByYear;
    }










    public HashMap<String, String>[] getLyrics(ArrayList<String>[] songs) {
        // go to azlyrics and scrap lyrics, put into hashmap where
        // key: name, value: lyrics
        // return array of hashmaps where each hash is an spot in array
        @SuppressWarnings("unchecked")
        HashMap<String, String>[] lyricsByYear = new HashMap[15];

        while (year < 2025) {
            ArrayList<String> currYearSongs = songs[year - 2010];
            HashMap<String, String> lyricsThisYear = new HashMap<>();

            for (String currYearSong : currYearSongs) {
                try {
                    Thread.sleep(1500); // 1.5 seconds between requests
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    getLyrics(currYearSong); // move new url
                    System.out.println("url: " + this.url.getURL());
                } catch (RuntimeException e) {
                    System.out.println("Error: unable to get lyrics for " + currYearSong + ": " + e.getMessage());
                    continue;
                }

                newExpression("<b>(\".*?\")</b><br>.*?that. -->\\s*(.*?)</div>");

                if (m.find()) {
                    String currSong = m.group(1);
                    String currLyrics = m.group(2).replaceAll("<br>", "");
                    currLyrics = currLyrics.replaceAll("<i>.*?</i>", "");
                    currLyrics = currLyrics.replaceAll("&quot;", "\"");

                    lyricsThisYear.put(currSong, currLyrics);
                } else {
                    System.out.println("Error: no lyrics matched for year " + year);
                }
            }
            lyricsByYear[year - 2010] = lyricsThisYear;
            year++;
        }
        return lyricsByYear;
    }

        // ======================================================

}