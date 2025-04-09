package com.example.moviesearch.View;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesearch.Model.Movie;
import com.example.moviesearch.databinding.ItemMovieBinding;

public class FavoritesAdapter extends ListAdapter<Movie, FavoritesAdapter.MovieViewHolder> {

    public FavoritesAdapter() {
        super(new DiffUtil.ItemCallback<Movie>() {
            @Override
            public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                return oldItem.getImdbID().equals(newItem.getImdbID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = getItem(position);
        holder.bind(movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie movie) {
            binding.textTitle.setText(movie.getTitle());
            binding.textYear.setText(movie.getYear());
            Glide.with(binding.getRoot().getContext())
                    .load(movie.getPoster())
                    .into(binding.imagePoster);

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), FavoriteMovieDetailActivity.class);
                intent.putExtra("MOVIE", movie);  // Pass the entire movie object
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }
}
