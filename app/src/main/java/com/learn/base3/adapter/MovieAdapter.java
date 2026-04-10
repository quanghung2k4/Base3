package com.learn.base3.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.base3.databinding.ItemMovieBinding;
import com.learn.base3.model.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movies;
    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(List<Movie> movies, OnMovieClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie movie, OnMovieClickListener listener) {
            binding.tvMovieTitle.setText(movie.getTitle());
            binding.tvMovieGenre.setText(movie.getGenre());
            binding.tvMovieRating.setText("⭐ " + movie.getRating());
            Glide.with(binding.ivMoviePoster.getContext())
                    .load(movie.getImageUrl())
                    .placeholder(com.learn.base3.R.drawable.ic_launcher_background)
                    .into(binding.ivMoviePoster);

            binding.getRoot().setOnClickListener(v -> listener.onMovieClick(movie));
        }
    }
}