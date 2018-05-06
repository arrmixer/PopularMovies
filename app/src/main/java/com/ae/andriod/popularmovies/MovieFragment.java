package com.ae.andriod.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.ae.andriod.popularmovies.Model.Movie;
import com.ae.andriod.popularmovies.Util.FetchMovies;
import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.FragmentMoviesBinding;
import com.ae.andriod.popularmovies.databinding.ListItemMovieBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment {

    //placeholder for Movie
    private Movie mMovie;

    private List<Movie> mMovieList;



    private FragmentMoviesBinding mFragmentMoviesBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchMoviesTask().execute();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*Using DataBinding class to inflate View into fragment
         * via the DataBindingUtil class*/

        mFragmentMoviesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);


        //creating a gridlayout to display image buttons
        mFragmentMoviesBinding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        setupAdapter();

        /*Returning the root of the DataBinding class
         * since that is where the view resides*/
        return mFragmentMoviesBinding.getRoot();


    }

    private class MovieHolder extends RecyclerView.ViewHolder {

        //get instance of DataBinding class
        private ListItemMovieBinding mListItemMovieBinding;



        /*adding instance of DataBinding class to
         * constructor */
        private MovieHolder(ListItemMovieBinding binding) {
            super(binding.getRoot());

            //assign instance of ListItemMovieBinding to parameter
            mListItemMovieBinding = binding;

            mListItemMovieBinding.setViewModel(new MovieViewModel());


        }

        public void bind(Movie movie) {
            mListItemMovieBinding.getViewModel().setMovie(movie);
            mListItemMovieBinding.getViewModel().getTitle();
            mListItemMovieBinding.executePendingBindings();
        }

        public void bindImageItem(Movie movie){
            mListItemMovieBinding.getViewModel().setMovie(movie);
            mListItemMovieBinding.executePendingBindings();
        }


    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private List<Movie> mMovies;

        public MovieAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //get inflater from container Activity
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            //inflate the view from the ListItemMovieBinding class
            ListItemMovieBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_movie, parent, false);

            return new MovieHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            mMovie = mMovies.get(position);
            Log.i("Activity", "" + mMovie.getImage(mMovie));
            holder.bindImageItem(mMovie);
//            holder.bind(mMovie);


        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, List<Movie>> {


        @Override
        protected List<Movie> doInBackground(Void... voids) {


            Log.i("Activity", FetchMovies.parseSandwichJson().toString());
            Log.i("Activity", "" + FetchMovies.parseSandwichJson().size());
            return FetchMovies.parseSandwichJson();


        }


        @Override
        protected void onPostExecute(List<Movie> movies) {
            mMovieList = movies;
            setupAdapter();

        }
    }

    //make sure data is in before assigning to adapter
    private void setupAdapter() {
        if (isAdded() && mMovieList != null) {
            mFragmentMoviesBinding.recyclerView.setAdapter(new MovieAdapter(mMovieList));
        }
    }


}
