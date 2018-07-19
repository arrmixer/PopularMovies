package com.ae.andriod.popularmovies.View;


import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

import static com.ae.andriod.popularmovies.View.MovieFragment.EXTRA_MOVIE;
import static com.ae.andriod.popularmovies.View.MovieFragment.EXTRA_MOVIE_LIST;
import static com.ae.andriod.popularmovies.View.MovieFragment.REQUEST_MOVIE_LIST;


public class MainActivity extends SingleFragmentActivity implements MovieFragment.Callbacks {

    @Override
    protected Fragment createFragment() {

        return new MovieFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        Log.i("main activity", "onCreate: ");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("main activity", "onDestroy: ");

    }


    @Override
    public void onMovieSelected(Movie movie, List<Movie> movies) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent i = new Intent(this, DetailActivity.class);
            i.putParcelableArrayListExtra(EXTRA_MOVIE_LIST, (ArrayList<? extends Parcelable>) movies);
            i.putExtra(EXTRA_MOVIE, movie);
            startActivityForResult(i, REQUEST_MOVIE_LIST);
        } else {
            Fragment newDetail = DetailFragment.newInstance(movie, movies);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}
