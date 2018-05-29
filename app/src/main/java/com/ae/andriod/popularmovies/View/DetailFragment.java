package com.ae.andriod.popularmovies.View;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.R;
import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.FragmentMovieDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    //Constant for Logging
    private static final String TAG = DetailFragment.class.getSimpleName();

    //Constant keys for Intents and save Instance States
    private static final String EXTRA_MOVIE = "com.ae.andriod.popularmovies.movie";
    private static final String EXTRA_MOVIE_LIST = "com.ae.andriod.popularmovies.movie_list";


    //Constants for Detail Query and Video query from MovieDB
    private static final String VIDEOS = "videos"; //get trailers

    //instance for Data Binding class for Fragment
    private FragmentMovieDetailBinding mFragmentMovieDetailBinding;


    //placeholder for Movie object and ViewModel
    private Movie mMovie;
    private MovieViewModel mMovieViewModel;

    /*Following of adding a static method to the Fragment Class
     * This method will put arguments in the Bundle and then
     * attached it to the fragment. In addition, this allows for
     * more modularity so that the fragment is dependant on its
     * container activity*/
    public static DetailFragment newIntance(Movie movie, List<Movie> movies) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_MOVIE, movie);
        bundle.putParcelableArrayList(EXTRA_MOVIE_LIST, (ArrayList<? extends Parcelable>) movies);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_MOVIE, mMovie);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState != null){
            mMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);
        }else{
            mMovie = getArguments().getParcelable(EXTRA_MOVIE);
        }


        returnResult();

        Log.i(TAG, mMovie.toString());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Using DataBinding class to inflate View into fragment
         * via the DataBindingUtil class*/
        mFragmentMovieDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);

        //helper method to popular data onto view
        updateUI(mFragmentMovieDetailBinding, mMovie);

        /*return the root of the databind class which is
         * the fragment xml itself*/
        return mFragmentMovieDetailBinding.getRoot();
    }


    /*This method helps populate the data from the movie object onto
     * the fragment. It takes two parameters the DataBinding class generated
     * from the XML layout and the Movie object coming from the intent of the container
     * activity. It sets the ViewModel object and then sets the views as needed.
     *
     * @param FragmentMovieDetailBinding fragmentMovieDetailBinding
     * @param Movie movie
     *
     * @return void
     * */
    private void updateUI(FragmentMovieDetailBinding fragmentMovieDetailBinding, Movie movie) {

        /*create instance of ViewModel, set it to the DataBinding Class
         * ,and then add the movie object pass from the activity into
         * the ViewModel*/
        mMovieViewModel = new MovieViewModel();
        fragmentMovieDetailBinding.setViewModel(mMovieViewModel);
        fragmentMovieDetailBinding.getViewModel().setMovie(movie);

        //placeholders for String data
        String rating = Double.toString(mMovieViewModel.getUserRating()) + "/10";
        String year = mMovieViewModel.getReleaseDate().substring(0, 4);


        fragmentMovieDetailBinding.movieTitleTextView.setText(mMovieViewModel.getTitle());
        fragmentMovieDetailBinding.movieRatingTextView.setText(rating);
        fragmentMovieDetailBinding.movieYearTextView.setText(year);
        fragmentMovieDetailBinding.movieDescriptionTextView.setText(mMovieViewModel.getDecription());


    }

    private void returnResult(){
        Intent intent = getActivity().getIntent();
        getActivity().setResult(Activity.RESULT_OK, intent);
    }


}
