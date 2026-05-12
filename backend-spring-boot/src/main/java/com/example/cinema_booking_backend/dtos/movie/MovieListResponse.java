package com.example.cinema_booking_backend.dtos.movie;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MovieListResponse {
    public UUID id;
    public String movieName;
    public String posterUrl;
    public LocalDate releaseDate;
    public List<String> selectedGenres;
    public List<String> selectedFormats;
    public Integer duration;
    public LocalDateTime createdAt;

    public MovieListResponse(UUID id, String movieName, String posterUrl, LocalDate releaseDate, List<String> selectedGenres, List<String> selectedFormat, Integer duration, LocalDateTime createdAt) {
        this.id = id;
        this.movieName = movieName;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.selectedGenres = selectedGenres;
        this.selectedFormats = selectedFormat;
        this.duration = duration;
        this.createdAt = createdAt;
    }
}
