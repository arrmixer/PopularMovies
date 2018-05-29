package com.ae.andriod.popularmovies.View;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.R;

import java.util.List;

public class DetailActivity extends SingleFragmentActivity {

    private final String TAG = DetailActivity.class.getSimpleName();

    //Constant keys for Intents and save Instance States
    private static final String EXTRA_MOVIE = "com.ae.andriod.popularmovies.movie";
    private static final String EXTRA_MOVIE_LIST = "com.ae.andriod.popularmovies.movie_list";

    @Override
    protected Fragment createFragment() {
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        List<Movie> movies = getIntent().getParcelableArrayListExtra(EXTRA_MOVIE_LIST);
        return DetailFragment.newIntance(movie, movies);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


    }
}
