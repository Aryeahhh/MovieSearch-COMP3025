package com.example.moviesearch.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesearch.Model.Movie;
import com.example.moviesearch.Utils.ApiUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieViewModel extends ViewModel {
    private final MutableLiveData<List<Movie>> movieList = new MutableLiveData<>();
    private final MutableLiveData<Movie> movieDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final OkHttpClient client = new OkHttpClient();

    public LiveData<List<Movie>> getMovies() { return movieList; }
    public LiveData<Movie> getMovieDetails() { return movieDetails; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchMovies(String query) {
        String url = ApiUtil.getSearchUrl(query);
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                errorMessage.postValue("Failed to fetch movies: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Server error: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body() != null ? response.body().string() : null;
                    if (jsonData != null) {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (jsonObject.has("Search")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Search");
                            List<Movie> movies = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject movieJson = jsonArray.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(movieJson.optString("Title"));
                                movie.setYear(movieJson.optString("Year"));
                                movie.setImdbID(movieJson.optString("imdbID"));
                                movie.setType(movieJson.optString("Type"));
                                movie.setPoster(movieJson.optString("Poster"));
                                movies.add(movie);
                            }
                            movieList.postValue(movies);
                        } else {
                            errorMessage.postValue("No movies found");
                        }
                    }
                } catch (JSONException e) {
                    errorMessage.postValue("Error parsing movie data");
                }
            }
        });
    }

    public void fetchMovieDetails(String imdbID) {
        String url = ApiUtil.getMovieDetailsUrl(imdbID);
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                errorMessage.postValue("Failed to fetch details: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Server error: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body() != null ? response.body().string() : null;
                    if (jsonData != null) {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        Movie movie = new Movie();
                        movie.setTitle(jsonObject.optString("Title"));
                        movie.setYear(jsonObject.optString("Year"));
                        movie.setRated(jsonObject.optString("Rated"));
                        movie.setReleased(jsonObject.optString("Released"));
                        movie.setRuntime(jsonObject.optString("Runtime"));
                        movie.setGenre(jsonObject.optString("Genre"));
                        movie.setDirector(jsonObject.optString("Director"));
                        movie.setWriter(jsonObject.optString("Writer"));
                        movie.setActors(jsonObject.optString("Actors"));
                        movie.setPlot(jsonObject.optString("Plot"));
                        movie.setPoster(jsonObject.optString("Poster"));
                        movie.setImdbRating(jsonObject.optString("imdbRating"));
                        movie.setImdbID(jsonObject.optString("imdbID"));
                        movie.setType(jsonObject.optString("Type"));
                        movie.setProduction(jsonObject.optString("Production"));

                        movieDetails.postValue(movie);
                    }
                } catch (JSONException e) {
                    errorMessage.postValue("Error parsing movie details");
                }
            }
        });
    }
}