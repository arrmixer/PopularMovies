package com.ae.andriod.popularmovies.Util;

import android.os.AsyncTask;

import com.ae.andriod.popularmovies.Model.Movie;

public class FetchMovieDetailAsyncTask extends AsyncTask<Movie, Void, Movie> {

    private Movie movie;

    @Override
    protected Movie doInBackground(Movie... movies) {

        return FetchMovies.parseMovieDetail(movies[0]);
    }


    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);
    }
}
