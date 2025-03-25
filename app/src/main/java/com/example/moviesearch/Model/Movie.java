package com.example.moviesearch.Model;

public class Movie {
    private String Title;
    private String Year;
    private String Rated;
    private String Released;
    private String Runtime;
    private String Genre;
    private String Director;
    private String Writer;
    private String Actors;
    private String Plot;
    private String Poster;
    private String imdbRating;
    private String imdbID;
    private String Type;
    private String Production;

    // Getters and Setters
    public String getTitle() { return Title; }
    public String getYear() { return Year; }
    public String getRated() { return Rated; }
    public String getReleased() { return Released; }
    public String getRuntime() { return Runtime; }
    public String getGenre() { return Genre; }
    public String getDirector() { return Director; }
    public String getWriter() { return Writer; }
    public String getActors() { return Actors; }
    public String getPlot() { return Plot; }
    public String getPoster() { return Poster; }
    public String getImdbRating() { return imdbRating; }
    public String getImdbID() { return imdbID; }
    public String getType() { return Type; }
    public String getProduction() { return Production; }

    public void setTitle(String title) { Title = title; }
    public void setYear(String year) { Year = year; }
    public void setRated(String rated) { Rated = rated; }
    public void setReleased(String released) { Released = released; }
    public void setRuntime(String runtime) { Runtime = runtime; }
    public void setGenre(String genre) { Genre = genre; }
    public void setDirector(String director) { Director = director; }
    public void setWriter(String writer) { Writer = writer; }
    public void setActors(String actors) { Actors = actors; }
    public void setPlot(String plot) { Plot = plot; }
    public void setPoster(String poster) { Poster = poster; }
    public void setImdbRating(String imdbRating) { this.imdbRating = imdbRating; }
    public void setImdbID(String imdbID) { this.imdbID = imdbID; }
    public void setType(String type) { Type = type; }
    public void setProduction(String production) { Production = production; }
}