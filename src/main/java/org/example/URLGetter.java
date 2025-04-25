package org.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class will use HTTP to get the contents of a page
 */
public class URLGetter {

    private URL url;
    private HttpURLConnection httpConnection;

    public URLGetter(String url)  {
        try {
            this.url = new URL(url);
//            httpConnection = new HttpURLConnection(url);

            URLConnection connection = this.url.openConnection();
            httpConnection = (HttpURLConnection) connection;

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will print the status code and message
     * from the connection.
     */
    public void printStatusCode() {
        try {
            int code = httpConnection.getResponseCode();
            String message = httpConnection.getResponseMessage();

            if (code < 200 || code > 299) {
                System.out.println(code + " : " + message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will get the HTML contents of a page.
     * It will return an arraylist of strings
     * @return the arraylist of strings from the HTML page.
     */
    public ArrayList<String> getContents() {
        ArrayList<String> contents = new ArrayList<>();

        try {
            Scanner in = new Scanner(httpConnection.getInputStream());
            
            while (in.hasNextLine()) {
                String line = in.nextLine();;
                contents.add(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return contents;
    }

    public String getURL() {
        return url.toString();
    }

    //    public URL getRedirectURL() {
    //        try {
    //            if (httpConnection.getResponseCode() >=300 ||
    //                    httpConnection.getResponseCode() < 400) {
    //                return new URL(httpConnection.getHeaderField("Location"));
    //            }
    //        } catch (IOException e) {
    //            throw new RuntimeException(e);
    //        }
    //    }

}
