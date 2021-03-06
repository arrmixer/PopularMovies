package com.ae.andriod.popularmovies.View;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.R;
import com.ae.andriod.popularmovies.Util.FetchMoviesAsyncTask;
import com.ae.andriod.popularmovies.Util.MenuQuery;
import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.FragmentMoviesBinding;
import com.ae.andriod.popularmovies.databinding.ListItemMovieBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MovieFragment extends Fragment {

    //Constant for Logging
    private static final String TAG = MovieFragment.class.getSimpleName();


    //Constant keys for Intents and save Instance States
    private static final String EXTRA_QUERY = "com.ae.andriod.popularmovies.movie.query";
    private static final String EXTRA_MOVIE = "com.ae.andriod.popularmovies.movie";
    private static final String EXTRA_MOVIE_LIST = "com.ae.andriod.popularmovies.movie_list";
    private static final int REQUEST_MOVIE_LIST = 101;


    //Constants for Search Queries from MovieDB
    private static final String POPULAR = "popular"; //for popular search
    private static final String TOP_RATED = "top_rated"; //for highest rated
    public static final String FAVORITE = "favorties"; //for favorites

    /*placeholders for Movie and return Lists of Movies
     * per Category: Most Popular and Top-Rated*/

    private List<Movie> mMovieList; //Movies from API
    private List<Movie> mMovieDBList; //Movies from DB

    //Placeholder for String query parameter
    private String query;

    //instance of Data Binding class for Fragment
    private FragmentMoviesBinding mFragmentMoviesBinding;

    //instance of ViewModel
    private MovieViewModel mMovieViewModel;

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_MOVIE_LIST, (ArrayList<? extends Parcelable>) mMovieList);
        outState.putString(EXTRA_QUERY, query);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE_LIST);
            query = savedInstanceState.getString(EXTRA_QUERY);
        } else {

            FetchMoviesAsyncTask movieAsync = new FetchMoviesAsyncTask();

            if (MenuQuery.getPrefSearchQuery(getActivity()) != null) {
                query = MenuQuery.getPrefSearchQuery(getActivity());
            } else {
                query = POPULAR;
            }

            movieAsync.execute(query);
            getMovieList(movieAsync);

        }

        mMovieViewModel.getLiveData(mMovieList).observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(TAG, "Movie List Changed noticed");
                setupAdapter(movies);
            }
        });


        mMovieViewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movies) {
                Log.d(TAG, "Movies from favorites " + movies.size());
                mMovieDBList = movies;
                if (query.equals(FAVORITE)) {
                    setupAdapter(mMovieDBList);
                }

            }
        });


        /*required to let the fragmentmanager know to recieve the callback
         * from onCreateOptionsMenu method. If set to false the menu resource
         * will not be visible*/
        setHasOptionsMenu(true);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*Using DataBinding class to inflate View into fragment
         * via the DataBindingUtil class*/

        mFragmentMoviesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        mFragmentMoviesBinding.setLifecycleOwner(this);


        //creating a gridlayout to display image buttons
        mFragmentMoviesBinding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        /*Returning the root of the DataBinding class
         * since that is where the view resides*/

        return mFragmentMoviesBinding.getRoot();


    }

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //get instance of DataBinding class android:id="@+id/movie_Image"
        private final ListItemMovieBinding mListItemMovieBinding;

        //placeholder for each movie object coming from the adaptor
        private Movie mMovie;


        /*adding instance of DataBinding class to
         * constructor */
        private MovieHolder(ListItemMovieBinding binding) {
            super(binding.getRoot());


            //assign instance of ListItemMovieBinding to parameter
            mListItemMovieBinding = binding;

            //set click event to each itemView
            itemView.setOnClickListener(this);

            //set the ViewModel object on ListItemMovieBinding
            mListItemMovieBinding.setViewModel(mMovieViewModel);


        }

        private void bindImageItem(Movie movie) {
            mMovie = movie;
            /*add the movie object to the ViewModel to display
             * the properties */
            mMovieViewModel.setMovie(movie);
            mMovieViewModel.getImageUrl().observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    MovieViewModel.loadImage(mListItemMovieBinding.movieImage, s);
                }
            });
            mListItemMovieBinding.executePendingBindings();
        }


        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), "clicked!", Toast.LENGTH_LONG).show();
//            Log.i(TAG, "clicked" + mMovie.getTitle() + " " + mMovie.getImage(mMovie));

            Intent i = new Intent(view.getContext(), DetailActivity.class);
            i.putParcelableArrayListExtra(EXTRA_MOVIE_LIST, (ArrayList<? extends Parcelable>) mMovieList);
            i.putExtra(EXTRA_MOVIE, mMovie);
            startActivityForResult(i, REQUEST_MOVIE_LIST);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private final List<Movie> mMovies;

        MovieAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //get inflater from container Activity
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            //inflate the view from the ListItemMovieBinding class
            final ListItemMovieBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_movie, parent, false);

            binding.setLifecycleOwner(getActivity());


            return new MovieHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            //create a movie object from the list
            Movie movie = mMovies.get(position);
            Log.d(TAG, "Check image......." + movie.getImage(movie));
            holder.bindImageItem(movie);


        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }
    }


    //make sure data is in before assigning to adapter
    private void setupAdapter(List<Movie> movies) {
        if (isAdded() && mMovieList != null) {
            MovieAdapter movieAdapter = new MovieAdapter(movies);
            mFragmentMoviesBinding.recyclerView.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        }
    }

    /*Method retrieves the movie list result from the AsyncTask
     * @param instance of FetchMoviesAsyncTask
     *
     * @return movie list
     *
     * */
    private void getMovieList(FetchMoviesAsyncTask fetchMovies) {
        try {
            mMovieList = fetchMovies.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MOVIE_LIST) {
            mMovieList = data.getParcelableArrayListExtra(EXTRA_MOVIE_LIST);
        }
    }

    /*Menu Configuration*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.highest_rated:
                if (!query.equals(TOP_RATED)) {
                    FetchMoviesAsyncTask moviesTaskTopRated = new FetchMoviesAsyncTask();
                    query = TOP_RATED;
                    MenuQuery.setPrefSearchQuery(getActivity(), query);
                    moviesTaskTopRated.execute(TOP_RATED);
                    getMovieList(moviesTaskTopRated);
                    setupAdapter(mMovieList);
                } else {
                    Toast.makeText(getActivity(), R.string.menu_toast, Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.most_popular:
                if (!query.equals(POPULAR)) {
                    FetchMoviesAsyncTask moviesTaskPopular = new FetchMoviesAsyncTask();
                    query = POPULAR;
                    MenuQuery.setPrefSearchQuery(getActivity(), query);
                    moviesTaskPopular.execute(POPULAR);
                    getMovieList(moviesTaskPopular);
                    setupAdapter(mMovieList);
                } else {
                    Toast.makeText(getActivity(), R.string.menu_toast, Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.favorties:
                if (!query.equals(FAVORITE)) {
                    query = FAVORITE;
                    MenuQuery.setPrefSearchQuery(getActivity(), query);
                    setupAdapter(mMovieDBList);
                    if (mMovieDBList.isEmpty()) {
                        Toast.makeText(getActivity(), "Your Favorite List is Empty.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.menu_toast, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }


}
