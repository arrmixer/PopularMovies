package com.ae.andriod.popularmovies.View;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.MovieClipAdapter;
import com.ae.andriod.popularmovies.MovieReviewAdapter;
import com.ae.andriod.popularmovies.R;
import com.ae.andriod.popularmovies.Util.FetchMovieDetailAsyncTask;
import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.FragmentMovieDetailBinding;
import com.ae.andriod.popularmovies.databinding.MovieClipItemBinding;
import com.ae.andriod.popularmovies.databinding.ReviewsBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ae.andriod.popularmovies.Util.PackageCheck.isPackageInstalled;


public class DetailFragment extends Fragment implements MovieClipAdapter.ItemClickListener {
    //Constant for Logging
    private static final String TAG = DetailFragment.class.getSimpleName();

    //Constant keys for Intents and save Instance States
    private static final String EXTRA_MOVIE = "com.ae.andriod.popularmovies.movie";
    private static final String EXTRA_MOVIE_LIST = "com.ae.andriod.popularmovies.movie_list";
    public static final String YOUTUBE = "com.google.android.youtube";


    //Constants for Detail Query and Video query from MovieDB
    private static final String VIDEOS = "videos"; //get trailers

    //instance for Data Binding class for Fragment
    private FragmentMovieDetailBinding mFragmentMovieDetailBinding;

    //placeholders for both recycler views (movie clips and movie reviews)
    RecyclerView recyclerViewMovieClips;
    RecyclerView recyclerViewMovieReviews;

    //placeholders for both Adapters (movie clips and movie reviews)
    private MovieClipAdapter mMovieClipAdapter;
    private MovieReviewAdapter mMovieReviewAdapter;

    //placeholder for Movie object and ViewModel
    private Movie mMovie;
    private MovieViewModel mMovieViewModel;

    private Movie m;


    /*Following of adding a static method to the Fragment Class
     * This method will put arguments in the Bundle and then
     * attached it to the fragment. In addition, this allows for
     * more modularity so that the fragment is dependant on its
     * container activity*/
    public static DetailFragment newInstance(Movie movie, List<Movie> movies) {
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

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);


        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);

        } else {
            mMovie = getArguments().getParcelable(EXTRA_MOVIE);
            FetchMovieDetailAsyncTask movieDetailAsyncTask = new FetchMovieDetailAsyncTask();
            movieDetailAsyncTask.execute(mMovie);
            mMovie = getUpdatedMovie(movieDetailAsyncTask);

        }


        mMovieViewModel.setMovie(mMovie);

        mMovieViewModel.getMovieLiveData(mMovie).observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                Log.d(TAG, "Movie Changed noticed");
                mMovie = movie;
                updateUI(mFragmentMovieDetailBinding, mMovie);
                setupAdapter();

            }
        });

        mMovieViewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(TAG, "Movies from favorites " + movies.size());
            }
        });

        mMovieViewModel.getMovieFromDB(mMovie.getMovieId()).observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (movie != null && (movie.getMovieId() == mMovie.getMovieId())) {
                    Log.d(TAG, "Movie is already in DB!!!!! ");

                    mFragmentMovieDetailBinding.movieMarkFavoriteButton.setText(R.string.button_unfavorite);
                } else {
                    mFragmentMovieDetailBinding.movieMarkFavoriteButton.setText(R.string.button_favorite);
                }

            }
        });


        returnResult();


        Log.i(TAG, mMovie.toString());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Using DataBinding class to inflate View into fragment
         * via the DataBindingUtil class*/
        mFragmentMovieDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);
        mFragmentMovieDetailBinding.setLifecycleOwner(this);

        //setup RecyclerViews with a Linear Layout
        recyclerViewMovieClips = mFragmentMovieDetailBinding.movieClipsRecyclerview;
        recyclerViewMovieClips.setLayoutManager(new LinearLayoutManager(getActivity()));


        recyclerViewMovieReviews = mFragmentMovieDetailBinding.movieReviewsRecyclerview;
        recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getActivity()));


        //helper method to popular data onto view
        updateUI(mFragmentMovieDetailBinding, mMovie);
        setupAdapter();

        mFragmentMovieDetailBinding.movieMarkFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "You clicked " + mMovie.getTitle(), Toast.LENGTH_SHORT).show();

                if(mFragmentMovieDetailBinding.movieMarkFavoriteButton.getText().toString().equals(getResources().getString(R.string.button_favorite))){
                    mMovieViewModel.insertMovieDB(mMovie);
                }else{
                    mMovieViewModel.deleteMovieDB(mMovie);
                }

            }
        });






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


        fragmentMovieDetailBinding.setViewModel(mMovieViewModel);
        mMovieViewModel.setMovie(movie);


        //placeholders for String data
        String rating = Double.toString(mMovieViewModel.getUserRating()) + "/10";
        String year = mMovieViewModel.getReleaseDate().substring(0, 4);
        String runtime = Integer.toString(mMovieViewModel.getRuntime()) + " min";


        fragmentMovieDetailBinding.movieTitleTextView.setText(mMovieViewModel.getTitle());
        fragmentMovieDetailBinding.movieRatingTextView.setText(rating);
        fragmentMovieDetailBinding.movieYearTextView.setText(year);
        fragmentMovieDetailBinding.movieDescriptionTextView.setText(mMovieViewModel.getDescription());
        fragmentMovieDetailBinding.movieDurationTextView.setText(runtime);


    }

    //make sure data is in before assigning to adapter
    private void setupAdapter() {

        if (isAdded() && mMovie.getYoutubeKeys() != null && mMovie.getReviews() != null) {

            //setup adapters

            mMovieClipAdapter = new MovieClipAdapter(this, getContext(), mMovieViewModel);

            recyclerViewMovieClips.setAdapter(mMovieClipAdapter);

            mMovieReviewAdapter = new MovieReviewAdapter(getContext(), mMovieViewModel);
            recyclerViewMovieReviews.setAdapter(mMovieReviewAdapter);

            mMovieClipAdapter.notifyDataSetChanged();
            mMovieReviewAdapter.notifyDataSetChanged();
        }
    }

    /*Method retrieves the update Movie model result from the AsyncTask.
     * The updated model has the movie runtime, a list of String reviews, and
     * a list of youtube keys to play the trailers.
     * @param instance of FetchMoviesAsyncTask
     *
     * @return updated movie
     *
     * */
    private Movie getUpdatedMovie(FetchMovieDetailAsyncTask fetchMovieDetailAsyncTask) {
        try {
            mMovie = fetchMovieDetailAsyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        return mMovie;
    }


    //returns back same list to Intent
    private void returnResult() {
        Intent intent = getActivity().getIntent();
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onItemClickListener(int itemId) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + mMovieViewModel.getYouTubeKeys().get(itemId)));

        String youtubePackage = YOUTUBE;
        if (isPackageInstalled(youtubePackage, getContext().getPackageManager())) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Need to install YouTube.", Toast.LENGTH_SHORT).show();
        }

    }

}
