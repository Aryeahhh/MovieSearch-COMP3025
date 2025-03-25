package com.example.moviesearch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesearch.Model.Movie;
import com.example.moviesearch.databinding.ItemMovieBinding;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final Context context;
    private final List<Movie> movies;
    private final OnMovieClickListener onMovieClickListener;

    public MovieAdapter(Context context, List<Movie> movies, OnMovieClickListener listener) {
        this.context = context;
        this.movies = movies;
        this.onMovieClickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieBinding binding = ItemMovieBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        if (movie == null) return;

        // Set basic movie info with null checks
        holder.binding.textTitle.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown Title");
        holder.binding.textYear.setText(movie.getYear() != null ? movie.getYear() : "Year N/A");

        // Load poster image safely
        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            Glide.with(context)
                    .load(movie.getPoster())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.binding.imagePoster);
        } else {
            holder.binding.imagePoster.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onMovieClickListener != null && movie.getImdbID() != null) {
                onMovieClickListener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ItemMovieBinding binding;

        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}