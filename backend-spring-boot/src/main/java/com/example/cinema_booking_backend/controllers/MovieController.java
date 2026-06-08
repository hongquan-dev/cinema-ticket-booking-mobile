package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.entity.Movie;
import com.example.cinema_booking_backend.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/coming-soon")
    public ResponseEntity<Map<String, Object>> getComingSoonMovies(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Movie> comingSoonMovies = movieService.getComingSoonMovies(date);

            response.put("message", "Fetched movie coming-soon list successfully");
            response.put("data", comingSoonMovies);
            Object total = response.put("total", comingSoonMovies.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}