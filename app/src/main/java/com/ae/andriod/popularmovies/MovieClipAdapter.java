package com.ae.andriod.popularmovies;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ae.andriod.popularmovies.View.DetailFragment;
import com.ae.andriod.popularmovies.ViewModel.MovieViewModel;
import com.ae.andriod.popularmovies.databinding.MovieClipItemBinding;

import java.util.List;


public class MovieClipAdapter extends RecyclerView.Adapter<MovieClipAdapter.MovieClipHolder> {

//     Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;

//    Class variables for the List that holds MovieViewModel and the Context
    private Context mContext;
    private MovieViewModel mMovieViewModel;

    public MovieClipAdapter(ItemClickListener itemClickListener, Context context, MovieViewModel movieViewModel) {
        mItemClickListener = itemClickListener;
        mContext = context;
        mMovieViewModel = movieViewModel;
    }

    @NonNull
    @Override
    public MovieClipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get inflater from container Activity
        LayoutInflater inflater = LayoutInflater.from(mContext);

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

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    class MovieClipHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


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

            int elementId = getAdapterPosition();
            mItemClickListener.onItemClickListener(elementId);


        }
    }

    //method use to check if phone has youtube installed
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}


