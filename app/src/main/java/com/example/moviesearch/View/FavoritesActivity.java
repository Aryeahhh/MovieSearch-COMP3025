package com.example.moviesearch.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesearch.Model.Movie;
import com.example.moviesearch.ViewModel.MovieViewModel;
import com.example.moviesearch.databinding.ActivityFavoritesBinding;
import com.example.moviesearch.databinding.ItemMovieBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private ActivityFavoritesBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private MovieViewModel movieViewModel;
    private com.example.moviesearch.View.FavoritesAdapter favoritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupRecyclerView();
        loadFavoriteMovies();

        binding.buttonSearchBottom.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, MovieSearchActivity.class);
            startActivity(intent);
        });

        binding.buttonFavoritesBottom.setOnClickListener(v -> {
            Toast.makeText(this, "Already in Favorites", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteMovies();
    }

    private void setupRecyclerView() {
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        favoritesAdapter = new com.example.moviesearch.View.FavoritesAdapter();
        binding.recyclerViewFavorites.setAdapter(favoritesAdapter);
    }

    private void loadFavoriteMovies() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            firestore.collection("favorites")
                    .document(userId)
                    .collection("movies")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Movie> favoriteMovies = queryDocumentSnapshots.toObjects(Movie.class);
                        favoritesAdapter.submitList(favoriteMovies);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
