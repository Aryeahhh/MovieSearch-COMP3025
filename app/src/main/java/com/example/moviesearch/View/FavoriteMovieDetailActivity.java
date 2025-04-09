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
import com.example.moviesearch.databinding.ActivityFavoriteMovieDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FavoriteMovieDetailActivity extends AppCompatActivity {
    private ActivityFavoriteMovieDetailBinding binding;
    private MovieViewModel movieViewModel;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupBackButton();

        // Get movie from intent
        movie = (Movie) getIntent().getSerializableExtra("MOVIE");
        if (movie != null) {
            populateMovieDetails(movie);
        } else {
            Toast.makeText(this, "No movie selected", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up Update button click listener to update plot
        binding.btnUpdate.setOnClickListener(v -> updateMoviePlot());

        // Set up Remove from Favorites button click listener
        binding.btnRemove.setOnClickListener(v -> removeFromFavorites());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload movie details from Firebase when the activity is resumed
        if (movie != null) {
            loadMovieDetails(movie.getImdbID());
        }
    }

    private void loadMovieDetails(String imdbID) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        firestore.collection("favorites")
                .document(userId)
                .collection("movies")
                .document(imdbID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        movie = documentSnapshot.toObject(Movie.class);
                        populateMovieDetails(movie);  // Update UI with the latest data
                    } else {
                        Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load movie details", Toast.LENGTH_SHORT).show();
                });
    }

    private void populateMovieDetails(Movie movie) {
        // Set movie details to the UI components
        binding.textTitle.setText(getSafeString(movie.getTitle()));
        binding.textYear.setText(getSafeString(movie.getYear()));
        binding.textRuntime.setText("Runtime: " + getSafeString(movie.getRuntime()));
        binding.textGenre.setText("Genre: " + getSafeString(movie.getGenre()));
        binding.textDirector.setText("Director: " + getSafeString(movie.getDirector()));
        binding.textActors.setText("Cast: " + getSafeString(movie.getActors()));
        binding.textPlot.setText(getSafeString(movie.getPlot()));  // Set the plot text
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

    private void updateMoviePlot() {
        String newPlot = binding.textPlot.getText().toString().trim();

        if (newPlot.isEmpty()) {
            Toast.makeText(this, "Plot cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        movie.setPlot(newPlot); // Update the plot in the movie object

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            firestore.collection("favorites")
                    .document(userId)
                    .collection("movies")
                    .document(movie.getImdbID())
                    .set(movie)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(FavoriteMovieDetailActivity.this, "Plot updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(FavoriteMovieDetailActivity.this, "Failed to update plot", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void removeFromFavorites() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            firestore.collection("favorites")
                    .document(userId)
                    .collection("movies")
                    .document(movie.getImdbID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(FavoriteMovieDetailActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity and return to the favorites list
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(FavoriteMovieDetailActivity.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
