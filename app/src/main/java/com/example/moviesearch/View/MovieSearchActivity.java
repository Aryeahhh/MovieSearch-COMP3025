package com.example.moviesearch.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moviesearch.Adapter.MovieAdapter;
import com.example.moviesearch.Model.Movie;
import com.example.moviesearch.ViewModel.MovieViewModel;
import com.example.moviesearch.databinding.ActivityMovieSearchBinding;

public class MovieSearchActivity extends AppCompatActivity {
    private ActivityMovieSearchBinding binding;
    private MovieViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupSearchButton();

        // Handling the Bottom Navigation Buttons
        binding.buttonSearchBottom.setOnClickListener(v -> {
            // Stay in the current activity (search activity)
            Toast.makeText(this, "Already in Search", Toast.LENGTH_SHORT).show();
        });

        binding.buttonFavoritesBottom.setOnClickListener(v -> {
            // Switch to Favorites Activity
            Intent intent = new Intent(MovieSearchActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
    }

    private void setupObservers() {
        movieViewModel.getMovies().observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                binding.recyclerView.setAdapter(new MovieAdapter(
                        this,
                        movies,
                        this::openMovieDetail
                ));
            }
        });

        movieViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchButton() {
        binding.buttonSearch.setOnClickListener(v -> {
            String query = binding.editSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                movieViewModel.fetchMovies(query);
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMovieDetail(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("IMDB_ID", movie.getImdbID());
        startActivity(intent);
    }
}
