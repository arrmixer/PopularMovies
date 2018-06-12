package com.ae.andriod.popularmovies.Util;

import android.os.AsyncTask;

import com.ae.andriod.popularmovies.Model.Movie;

import java.util.List;

public class FetchMoviesAysncTask extends AsyncTask<String, Void, List<Movie>> {

    //field for String query
    private String query;

    //field for Movie List
    private List<Movie> mMovieList;


    @Override
    protected List<Movie> doInBackground(String... strings) {

        //make query point to String parameter
        query = strings[0];


//            Log.i("Activity", FetchMovies.parseMoviesJson(strings[0]).toString());
//            Log.i("Activity", "" + FetchMovies.parseMoviesJson(strings[0]).size());
        return FetchMovies.parseMoviesJson(strings[0]);


    }


    @Override
    protected void onPostExecute(List<Movie> movies) {
        mMovieList = movies;


    }

}
