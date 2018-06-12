package com.ae.andriod.popularmovies.View;

import android.app.Activity;
import android.content.Intent;
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
import com.ae.andriod.popularmovies.R;
import com.ae.andriod.popularmovies.Util.FetchMovieDetailAsyncTask;
import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.FragmentMovieDetailBinding;
import com.ae.andriod.popularmovies.databinding.MovieClipItemBinding;
import com.ae.andriod.popularmovies.databinding.ReviewsBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DetailFragment extends Fragment {
    //Constant for Logging
    private static final String TAG = DetailFragment.class.getSimpleName();

    //Constant keys for Intents and save Instance States
    private static final String EXTRA_MOVIE = "com.ae.andriod.popularmovies.movie";
    private static final String EXTRA_MOVIE_LIST = "com.ae.andriod.popularmovies.movie_list";
    private static final String EXTRA_MOVIE_CLIP_LIST = "com.ae.andriod.popularmovies.movie_clip_list";


    //Constants for Detail Query and Video query from MovieDB
    private static final String VIDEOS = "videos"; //get trailers

    //instance for Data Binding class for Fragment
    private FragmentMovieDetailBinding mFragmentMovieDetailBinding;

    //placeholders for both recycler views (movie clips and movie reviews)
    RecyclerView recyclerViewMovieClips;
    RecyclerView recyclerViewMovieReviews;

    //placeholder for Movie object and ViewModel
    private Movie mMovie;
    private MovieViewModel mMovieViewModel;
    private List<String> movieClips;
    private List<String> authors;
    private List<String> reviews;

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

//        RecyclerView mRecylerView = mFragmentMovieDetailBinding.recyclerViewClips;
//        if(mRecylerView.getLayoutManager().onSaveInstanceState() != null){
//            outState.putParcelable("test",mRecylerView.getLayoutManager().onSaveInstanceState() );
//
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);

        } else {
            mMovie = getArguments().getParcelable(EXTRA_MOVIE);
            FetchMovieDetailAsyncTask movieDetailAsyncTask = new FetchMovieDetailAsyncTask();
            movieDetailAsyncTask.execute(mMovie);
            mMovie = getUpdatedMovie(movieDetailAsyncTask);

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

        //setup RecyclerViews with a Linear Layout
        recyclerViewMovieClips = mFragmentMovieDetailBinding.movieClipsRecyclerview;
        recyclerViewMovieClips.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewMovieReviews = mFragmentMovieDetailBinding.movieReviewsRecyclerview;
        recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getActivity()));

        //helper method to popular data onto view
        updateUI(mFragmentMovieDetailBinding, mMovie);

        setupAdapter();


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
        String runtime = Integer.toString(mMovieViewModel.getRuntime()) + " min";


        fragmentMovieDetailBinding.movieTitleTextView.setText(mMovieViewModel.getTitle());
        fragmentMovieDetailBinding.movieRatingTextView.setText(rating);
        fragmentMovieDetailBinding.movieYearTextView.setText(year);
        fragmentMovieDetailBinding.movieDescriptionTextView.setText(mMovieViewModel.getDecription());
        fragmentMovieDetailBinding.movieDurationTextView.setText(runtime);


    }

    //make sure data is in before assigning to adapter
    private void setupAdapter() {

        if (isAdded() && mMovie.getYoutubeKeys() != null && mMovie.getReviews() != null) {

            //setup adapters
            MovieClipAdapter movieClipAdapter = new MovieClipAdapter();
            recyclerViewMovieClips.setAdapter(movieClipAdapter);

            MovieReviewAdapter movieReviewAdapter = new MovieReviewAdapter();
            recyclerViewMovieReviews.setAdapter(movieReviewAdapter);

            movieClipAdapter.notifyDataSetChanged();
            movieReviewAdapter.notifyDataSetChanged();
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


    //RecyclerViews one for the Movie Clips and one for the Reviews
    private class MovieClipHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final MovieClipItemBinding mMovieClipItemBinding;


        private MovieClipHolder(MovieClipItemBinding binding) {
            super(binding.getRoot());

            //assign instance of ListItemMovieDetailBinding to parameter
            mMovieClipItemBinding = binding;

            //set click event to each itemView
            itemView.setOnClickListener(this);

            //set the same ViewModel object on mListItemMovieDetailBinding
            mMovieClipItemBinding.setViewModel(mMovieViewModel);

        }

        private void bindMovieClip(int position) {

            String message = "Movie Clip " + (position + 1);
            mMovieClipItemBinding.textView.setText(message);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Toast.makeText(getContext(), "Clicked" + mMovieViewModel.getYouTubeKeys() + " " + getAdapterPosition(), Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + mMovieViewModel.getYouTubeKeys().get(getAdapterPosition())));
            startActivity(intent);

        }
    }

    private class MovieClipAdapter extends RecyclerView.Adapter<MovieClipHolder> {
//
//        private List<String> movieClips;
//
//        public MovieClipAdapter(List<String> movieClips) {
//            this.movieClips = movieClips;
//        }

        @NonNull
        @Override
        public MovieClipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //get inflater from container Activity
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            //inflate the view from the ListItemMovieBinding class
            MovieClipItemBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.movie_clip_item, parent, false);


            return new MovieClipHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieClipHolder holder, int position) {
            holder.bindMovieClip(position);
        }

        @Override
        public int getItemCount() {
            return mMovieViewModel.getYouTubeKeys().size();
        }
    }

    private class MovieReviewHolder extends RecyclerView.ViewHolder {

        //get instance of databinding class
        private final ReviewsBinding mReviewsBinding;

        public MovieReviewHolder(ReviewsBinding binding) {
            super(binding.getRoot());

            //assign instance of mReviewBindng to parameter
            mReviewsBinding = binding;

            //set the same ViewModel object on mReviewsBinding
            mReviewsBinding.setViewModel(mMovieViewModel);
        }


        private void bindReviews(int position) {

            if(mMovieViewModel.getAuthors().size() > 0){
                mReviewsBinding.author.setText(mMovieViewModel.getAuthors().get(position));
                mReviewsBinding.authorReview.setText(mMovieViewModel.getMovieReviews().get(position));
            }else{
                mReviewsBinding.author.setText("No reviews at this time.");
            }


        }
    }

    private class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewHolder> {

        @NonNull
        @Override
        public MovieReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //get inflater from container Activity
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            //inflate the view from the ReviewsBinding class
            ReviewsBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.reviews, parent, false);


            return new MovieReviewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieReviewHolder holder, int position) {
            holder.bindReviews(position);
        }

        @Override
        public int getItemCount() {
            //could have used either List: authors or reviews
            return mMovieViewModel.getMovieReviews().size();
        }
    }


}
