package com.ae.andriod.popularmovies.ViewModel;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;
import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.R;

import com.squareup.picasso.Picasso;

public class MovieViewModel extends BaseObservable {

    private final String TAG = MovieViewModel.class.getSimpleName();

    private Movie mMovie;

    public MovieViewModel() {
    }

    public int getId() {
        return mMovie.getMovieId();
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
        notifyChange();
    }

    @Bindable
    public String getTitle() {
        return mMovie.getTitle();
    }

    @Bindable
    public double getUserRating() {
        return mMovie.getUserRating();
    }

    @Bindable
    public String getReleaseDate() {
        return mMovie.getReleaseDate();
    }

    @Bindable
    public String getDecription() {
        return mMovie.getDescription();
    }

    public String getImageUrl() {
        return mMovie.getImage(mMovie);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.ic_udacity)
                .into(view);
    }


}
