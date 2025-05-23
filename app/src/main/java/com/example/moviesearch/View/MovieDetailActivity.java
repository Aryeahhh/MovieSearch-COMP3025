package com.example.moviesearch.View;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.moviesearch.Model.Movie;
import com.example.moviesearch.ViewModel.MovieViewModel;
import com.example.moviesearch.databinding.ActivityMovieDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailActivity extends AppCompatActivity {
    private ActivityMovieDetailBinding binding;
    private MovieViewModel movieViewModel;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupBackButton();
        setupObservers();

        // Get movie ID from intent
        String imdbID = getIntent().getStringExtra("IMDB_ID");
        if (imdbID != null) {
            movieViewModel.fetchMovieDetails(imdbID);
        } else {
            Toast.makeText(this, "No movie selected", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up Add to Favorites button click listener
        binding.btnAddToFavorites.setOnClickListener(v -> addMovieToFavorites());
    }

    private void setupObservers() {
        movieViewModel.getMovieDetails().observe(this, movie -> {
            if (movie != null) {
                // Set basic info with null checks
                binding.textTitle.setText(getSafeString(movie.getTitle()));
                binding.textYear.setText(getSafeString(movie.getYear()));
                binding.textRuntime.setText("Runtime: " + getSafeString(movie.getRuntime()));
                binding.textGenre.setText("Genre: " + getSafeString(movie.getGenre()));
                binding.textDirector.setText("Director: " + getSafeString(movie.getDirector()));
                binding.textActors.setText("Cast: " + getSafeString(movie.getActors()));
                binding.textPlot.setText(getSafeString(movie.getPlot()));
                binding.textProduction.setText("Produced by: " + getSafeString(movie.getProduction()));

                // Handle optional fields
                if (binding.textRated != null) {
                    binding.textRated.setText("Rated: " + getSafeString(movie.getRated()));
                }
                if (binding.textReleased != null) {
                    binding.textReleased.setText("Released: " + getSafeString(movie.getReleased()));
                }
                if (binding.textWriter != null) {
                    binding.textWriter.setText("Writer: " + getSafeString(movie.getWriter()));
                }

                // Set rating with color
                try {
                    float rating = Float.parseFloat(movie.getImdbRating());
                    binding.textRating.setText(String.format("%.1f/10", rating));

                    if (rating >= 7.0) {
                        binding.textRating.setTextColor(Color.GREEN);
                    } else if (rating >= 5.0) {
                        binding.textRating.setTextColor(Color.parseColor("#FFA500")); // Orange
                    } else {
                        binding.textRating.setTextColor(Color.RED);
                    }
                } catch (NumberFormatException e) {
                    binding.textRating.setText("Rating: N/A");
                    binding.textRating.setTextColor(Color.GRAY);
                }

                // Load poster image
                Glide.with(this)
                        .load(movie.getPoster())
                        .into(binding.imagePoster);
            }
        });

        movieViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBackButton() {
        if (binding.toolbar != null) {
            setSupportActionBar(binding.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private String getSafeString(String value) {
        return value != null ? value : "N/A";
    }

    private void addMovieToFavorites() {
        Movie movie = movieViewModel.getMovieDetails().getValue();
        if (movie != null && auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            firestore.collection("favorites")
                    .document(userId)
                    .collection("movies")
                    .document(movie.getImdbID())
                    .set(movie)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "You need to be logged in to add favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
