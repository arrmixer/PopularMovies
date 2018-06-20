package com.ae.andriod.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.ReviewsBinding;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewHolder> {

//    Class variables for the List that holds MovieViewModel and the Context
    private Context mContext;
    private MovieViewModel mMovieViewModel;

    public MovieReviewAdapter(Context context, MovieViewModel movieViewModel) {
        mContext = context;
        mMovieViewModel = movieViewModel;
    }

    @NonNull
    @Override
    public MovieReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get inflater from container Activity
        LayoutInflater inflater = LayoutInflater.from(mContext);

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

    class MovieReviewHolder extends RecyclerView.ViewHolder {

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

            if (!mMovieViewModel.getAuthors().isEmpty()) {
                mReviewsBinding.author.setText(mMovieViewModel.getAuthors().get(position));
                mReviewsBinding.authorReview.setText(mMovieViewModel.getMovieReviews().get(position));
            } else {
                mReviewsBinding.author.setText("No reviews at this time.");
            }


        }
    }
}
