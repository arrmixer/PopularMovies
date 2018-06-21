package com.ae.andriod.popularmovies.ViewModel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.MovieRepository;
import com.ae.andriod.popularmovies.R;

import com.squareup.picasso.Picasso;


import java.util.List;


public class MovieViewModel extends AndroidViewModel {

    private final String TAG = MovieViewModel.class.getSimpleName();


    private Movie mMovie;

    private final MutableLiveData<List<Movie>> mLiveData;
    private final MutableLiveData<Movie> mMovieLiveData;

    private final MovieRepository mMovieRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mMovieRepository = new MovieRepository(application);
        mLiveData = new MutableLiveData<>();
        mMovieLiveData = new MutableLiveData<>();
    }


    public void setMovie(Movie movie) {
        mMovie = movie;

    }

    public MutableLiveData<List<Movie>> getLiveData(List<Movie> movies) {
        mLiveData.setValue(movies);
        return mLiveData;
    }

    public LiveData<Movie> getMovieLiveData(Movie movie) {
        mMovieLiveData.setValue(movie);
        return mMovieLiveData;
    }

    public int getId() {
        return mMovie.getMovieId();
    }

    public String getTitle() {
        return mMovie.getTitle();
    }


    public double getUserRating() {
        return mMovie.getUserRating();
    }


    public String getReleaseDate() {
        return mMovie.getReleaseDate();
    }


    public List<String> getAuthors() {
        return mMovie.getAuthors();
    }


    public List<String> getMovieReviews() {
        return mMovie.getReviews();
    }


    public List<String> getYouTubeKeys() {
        return mMovie.getYoutubeKeys();
    }


    public int getRuntime() {
        return mMovie.getRuntime();
    }


    public String getDescription() {
        return mMovie.getDescription();
    }

    public MutableLiveData<String> getImageUrl() {
        MutableLiveData<String> string = new MutableLiveData<>();
        string.setValue(mMovie.getImage(mMovie));
        return string;
    }


    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_udacity)
                .into(view);
    }


    //DB operations

    public void insertMovieDB(Movie movie){

        mMovieRepository.insertMovieDB(movie);
    }

    public LiveData<List<Movie>> getAllMovies(){

       return mMovieRepository.getAllFavoriteMovies();
    }

    public void deleteMovieDB(Movie movie){
        mMovieRepository.deleteMovieDB(movie);
    }

    public LiveData<Movie> getMovieFromDB(int movieId){
        return mMovieRepository.getMovieFromDB(movieId);
    }
}
