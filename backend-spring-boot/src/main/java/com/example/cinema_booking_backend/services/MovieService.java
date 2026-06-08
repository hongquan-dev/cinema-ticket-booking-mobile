package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.entity.Movie;
import com.example.cinema_booking_backend.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getComingSoonMovies(LocalDate date) {
        try {
            List<Movie> movies = movieRepository.findByReleaseDateAfterOrderByReleaseDateAsc(date);

            return movies;
        } catch (Exception e) {
            throw new RuntimeException("Error when fetching movie coming-soon list: " + e.getMessage());
        }
    }
}