package com.example.moviesearch.Utils;

public class ApiUtil {
    private static final String API_KEY = "744c8dbb";
    private static final String BASE_URL = "https://www.omdbapi.com/"; // Changed to HTTPS

    public static String getSearchUrl(String query) {
        return BASE_URL + "?apikey=" + API_KEY + "&s=" + query + "&r=json";
    }

    public static String getMovieDetailsUrl(String imdbID) {
        return BASE_URL + "?apikey=" + API_KEY + "&i=" + imdbID + "&r=json&plot=full";
    }
}