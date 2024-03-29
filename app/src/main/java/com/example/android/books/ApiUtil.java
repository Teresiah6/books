package com.example.android.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class ApiUtil {
    //contains only static methods and thus remove the constructor
    private ApiUtil() {}

    //put part of the URL that is not going to change
    public static final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY ="";

    public static URL buildURL(String title) {
        // changing part of URL inserted here

        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon().
                appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());// change Url to uri
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    //connect
    public static String getJson(URL url) throws IOException {

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            // allows us to read any data
            InputStream stream = connection.getInputStream();
            //convert stream to string
            Scanner scanner = new Scanner(stream);
            //read everything
            // limit large streams of data into smaller ones
            scanner.useDelimiter("//A"); // pattern and regular expressions

            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }catch (Exception e){
            Log.d("Error", e.toString());//logs the error
            return null;
        }
        finally {
            connection.disconnect();// disconnects the connection
        }
    }
    public static ArrayList<Book> getBooksFromJson(String json) {
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE="publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";

        ArrayList<Book> books = new ArrayList<Book>();
        try {
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();

            for (int i =0; i<numberOfBooks;i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON =
                        bookJSON.getJSONObject(VOLUMEINFO);
                int authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                String[] authors = new String[authorNum];
                for (int j=0; j<authorNum;j++) {
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }
                Book book = new Book(
                        bookJSON.getString(ID),
                        volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE)?"":volumeInfoJSON.getString(SUBTITLE)),
                        authors,
                        volumeInfoJSON.getString(PUBLISHER),
                        volumeInfoJSON.getString(PUBLISHED_DATE));
                books.add(book);

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }



        return books;
    }
}
