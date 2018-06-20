package com.ae.andriod.popularmovies.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.ae.andriod.popularmovies.Model.Movie;

import java.util.List;

public class FetchMoviesAsyncTask extends AsyncTask<String, Void, List<Movie>> {

    private static final String TAG = FetchMoviesAsyncTask.class.getSimpleName();

    //field for String query
    private String query;

    //field for Movie List
    private List<Movie> mMovieList;


    @Override
    protected List<Movie> doInBackground(String... strings) {

        //make query point to String parameter
        query = strings[0];


            Log.d(TAG, FetchMovies.parseMoviesJson(strings[0]).toString());
            Log.d(TAG, "" + FetchMovies.parseMoviesJson(strings[0]).size());
        return FetchMovies.parseMoviesJson(strings[0]);


    }


    @Override
    protected void onPostExecute(List<Movie> movies) {
        mMovieList = movies;


    }

}
