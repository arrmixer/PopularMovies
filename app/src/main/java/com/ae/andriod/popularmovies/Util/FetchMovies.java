package com.ae.andriod.popularmovies.Util;

import android.net.Uri;
import android.util.Log;

import com.ae.andriod.popularmovies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/*Util class used to open HTTP connection and Parse
 * the JSON data*/
public class FetchMovies {

    private static final String TAG = FetchMovies.class.getSimpleName();



    //key
    private static final String API_KEY = "add your key";

    //Constants for URI
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String POPULAR = "popular"; //for popular search


    //API Uri converted to a string
    private static final String ENDPOINT = Uri
            .parse(BASE_URL)
            .buildUpon()
            .appendPath(POPULAR)
            .appendQueryParameter("api_key", API_KEY)
            .build().toString();


    //JSON fields
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String USER_RATING = "vote_average"; //same as user rating
    private static final String POSTER = "poster_path";
    private static final String OVERVIEW = "overview"; // same as description
    private static final String RELEASE_DATE = "release_date";


    /*Method opens a HTTP connection using HttpUrLConnection class, decided to
     * use the ByteArrayOutputStream Method since it has a performance boost
     * over using the scanner with a useDelimiter("\\A") trick. This method takes
     * the output from the ByteArrayOutputStream and converts it to a String
     *
     * @param urlSpec String of URL
     *
     * @return String conversion of byte[] from output stream
     *
     * @throws IOException if connection cannot be established */
    public static byte[] getUrlBytes() throws IOException {

        URL url = new URL(ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()
                        + ": with " +
                        ENDPOINT);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    //TODO need to document properly
    /**/
    public static ArrayList<Movie> parseSandwichJson() {

        //Data Structure to hold the Movie objects
        ArrayList<Movie> movies = new ArrayList<>();

        try {

            String json = new String(FetchMovies.getUrlBytes());

            /* place holders for all the fields within the Movie class */
            int movieID;
            String title;
            double userRating;
            String releaseDate;
            String posterPath;
            String description;

            //get JSON result and array
            JSONObject jsonObject = new JSONObject(json);
            JSONArray results = jsonObject.getJSONArray("results");

            /*Iterate through JSON to get desired data, construct a
             * new movie object, and put it in the list. */
            for (int i = 0; i < results.length(); i++) {
                movieID = results.getJSONObject(i).getInt(ID);
                title = results.getJSONObject(i).getString(TITLE);
                userRating = results.getJSONObject(i).getDouble(USER_RATING);
                releaseDate = results.getJSONObject(i).getString(RELEASE_DATE);
                posterPath = results.getJSONObject(i).getString(POSTER);
                description = results.getJSONObject(i).getString(OVERVIEW);

                movies.add(new Movie(movieID, title, posterPath, userRating, releaseDate, description));


            }
            return movies;
        } catch (IOException ie) {
            Log.e(TAG, "Failed to fetch items: -----------------", ie);
        }catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return movies;
    }





}
