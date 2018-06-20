package com.ae.andriod.popularmovies;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.database.AppDatabase;
import com.ae.andriod.popularmovies.database.MovieDao;


import java.util.List;

public class MovieRepository {

    public static final String TAG = MovieRepository.class.getSimpleName();

    private MovieDao mMovieDao;
    private LiveData<List<Movie>> mAllFavoriteMovies;
    private LiveData<Movie> mMovieLiveData;

    public MovieRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        mMovieDao = db.mMovieDao();
        mAllFavoriteMovies = db.mMovieDao().loadAllMovies();

    }

    public LiveData<List<Movie>> getAllFavoriteMovies() {
        return mAllFavoriteMovies;
    }

    public void insertMovieDB (Movie movie) {

        new insertAsyncTask(mMovieDao).execute(movie);
    }

    public void deleteMovieDB(Movie movie){
        new deleteAsyncTask(mMovieDao).execute(movie);
    }

    public LiveData<Movie> getMovieFromDB(int movieId){

        return mMovieDao.loadMovieById(movieId);
    }


    //all AysncTasks for the movie queries

    private static class insertAsyncTask extends AsyncTask<Movie, Void, Void> {

        private MovieDao mAsyncMovieDao;

        insertAsyncTask(MovieDao dao) {
            mAsyncMovieDao = dao;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            mAsyncMovieDao.insertMovie(params[0]);
            return null;
        }

    }

    private static class deleteAsyncTask extends AsyncTask<Movie, Void, Void> {

        private MovieDao mAsyncMovieDao;

        deleteAsyncTask(MovieDao dao) {
            mAsyncMovieDao = dao;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            mAsyncMovieDao.deleteMovie(params[0]);
            return null;
        }
    }

//    private static class getMovieWithIdAsyncTask extends AsyncTask<Integer, Void, LiveData<Movie>> {
//
//        private MovieDao mAsyncMovieDao;
//
//        getMovieWithIdAsyncTask(MovieDao dao) {
//            mAsyncMovieDao = dao;
//        }
//
//        @Override
//        protected LiveData<Movie> doInBackground(Integer... integers) {
//            return mAsyncMovieDao.loadMovieById(integers[0]);
//        }
//
//    }

}
