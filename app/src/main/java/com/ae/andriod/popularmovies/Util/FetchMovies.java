package com.ae.andriod.popularmovies.Util;

import android.net.Uri;
import android.util.Log;

import com.ae.andriod.popularmovies.BuildConfig;
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
import java.util.Map;


/*Util class used to open HTTP connection and Parse
 * the JSON data*/
public class FetchMovies {

    private static final String TAG = FetchMovies.class.getSimpleName();


    //key
    private static final String API_KEY = BuildConfig.Apikey;

    //Constants for URI
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String APPEND_TO_RESPONSE = "append_to_response"; //use append_to_response feature
    private static final String VIDEOS = "videos"; //get movie trailers
    private static final String REVIEWS = "reviews"; //get movie reviews


    //API Uri converted to a string
    private static final Uri ENDPOINT = Uri
            .parse(BASE_URL);


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
     * @param String query to get specific search API
     *
     * @return String conversion of byte[] from output stream
     *
     * @throws IOException if connection cannot be established */
    private static byte[] getUrlBytes(String query) throws IOException {

        String stringUrl = ENDPOINT
                .buildUpon()
                .appendPath(query)
                .appendQueryParameter("api_key", API_KEY)
                .build().toString();
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

//        Log.i(TAG, "New Url: =======" + stringUrl);

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()
                        + ": with " +
                        ENDPOINT);
            }

            int bytesRead;
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


    /* Method takes the data from the getUrlBytes method and converts the data
     * into a JSON object. Then takes the  each JSON object (20 as of 6/2018)
     * parses it into the Model (Movie). Each Model is placed into the Data Structure
     * (ArrayList).
     *
     * @param query string of URl from MovieDB
     *
     * @return A list of Movies coming from MoiveDB depending on the query*/
    public static ArrayList<Movie> parseMoviesJson(String query) {

        //Data Structure to hold the Movie objects
        ArrayList<Movie> movies = new ArrayList<>();

        try {

            String json = new String(FetchMovies.getUrlBytes(query));

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
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return movies;
    }

    //Movie Detail section


    /*Method opens a HTTP connection using HttpUrLConnection class, decided to
     * use the ByteArrayOutputStream Method since it has a performance boost
     * over using the scanner with a useDelimiter("\\A") trick. This method takes
     * the output from the ByteArrayOutputStream and converts it to a String
     *
     * @param int of Id of movie to be queried
     *
     *
     * @return String conversion of byte[] from output stream
     *
     * @throws IOException if connection cannot be established */
    private static byte[] getUrlBytesOfMovieDetail(int movieId) throws IOException {


        String stringUrl = ENDPOINT
                .buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(APPEND_TO_RESPONSE, VIDEOS)
                .build().toString();

        //needed to use StringBuilder to append path with ","
        StringBuilder sb = new StringBuilder();
        String string = sb.append(stringUrl).append(",").append(REVIEWS).toString();

//        Log.i(TAG, "New Url: =======" + string);


        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()
                        + ": with " +
                        ENDPOINT);
            }

            int bytesRead;
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

    /* Method takes the data from the getUrlBytes method and converts the data
     * into a JSON object. The JSON result has a total of three api calls thanks
     * to the "append to response" feature within the MovieDB api. The method
     * parses the info for the Movie runtime, user reviews, and youtube String keys.
     * Each piece of data is inserted into the Movie object passed into the method.
     *
     * @param instance movie object coming from the list of movies
     *
     * @return updated Movie object
     * */
    public static Movie parseMovieDetail(Movie movie) {

        try {

            //place holders for Movie Model
            int runtimeMovie;
            List<String> authors = new ArrayList<>();
            List<String> reviews = new ArrayList<>();
            List<String> youtubeKeys = new ArrayList<>();


            String json = new String(FetchMovies.getUrlBytesOfMovieDetail(movie.getMovieId()));
//            Log.i(TAG, "New Url: =======" + json);

            JSONObject jsonObject = new JSONObject(json);

            //parsing movie runtime
            runtimeMovie = jsonObject.getInt("runtime");
            movie.setRuntime(runtimeMovie);
//            Log.i(TAG, "New runtime: ======= " + movie.getRuntime());

            /*Parsing Video results and adding to Videos List*/
            JSONObject videos = jsonObject.getJSONObject("videos");
            JSONArray videosResult = videos.getJSONArray("results");

            for(int i = 0;i<videosResult.length();i++){
                String trailerKey = videosResult.getJSONObject(i).getString("key");
                youtubeKeys.add(trailerKey);
//                Log.i(TAG, "New Trailer: ======= https://www.youtube.com/watch?v=" + trailerKey);
//                Log.i(TAG, "New Trailer: ======= https://www.youtube.com/watch?v=" + youtubeKeys.get(i));
            }

            movie.setYoutubeKeys(youtubeKeys);

//            Log.i(TAG, "New youtubeKeys: ======= size is  " + movie.getYoutubeKeys().size());



            /*Parsing Review results and adding to Review List*/
            JSONObject reviewsJsonObject = jsonObject.getJSONObject("reviews");
            JSONArray reviewResults = reviewsJsonObject.getJSONArray("results");

            for(int i = 0;i<reviewResults.length();i++){
                String author = reviewResults.getJSONObject(i).getString("author");
                String movieReview = reviewResults.getJSONObject(i).getString("content");
                authors.add(author);
                reviews.add(movieReview);
//                Log.i(TAG,"New Author: ======= " + author);
//                Log.i(TAG,"New Review: ======= " + movieReview);
            }

            movie.setAuthors(authors);
            movie.setReviews(reviews);

//            Log.i(TAG, "New Reviews: ======= size is  " + movie.getReviews().size());



        } catch (IOException ie) {
            Log.e(TAG, "Failed to fetch items: -----------------", ie);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return movie;
    }

}
