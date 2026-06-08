package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.dto.showtime.MovieShowtimeResponse;
import com.example.cinema_booking_backend.entity.Showtime;
import com.example.cinema_booking_backend.services.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Showtime showtime = showtimeService.getShowtimeById(id);

            response.put("message", "Fetched showtime details successfully");
            response.put("data", showtime);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/grouped")
    public ResponseEntity<Map<String, Object>> getGrouped(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MovieShowtimeResponse> groupedData =
                    showtimeService.getGroupedShowtimesByCinemaAndDate(cinemaId, date);

            response.put("message", "Fetched grouped showtimes for cinema successfully");
            response.put("data", groupedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/grouped/{movieId}")
    public ResponseEntity<Map<String, Object>> getGroupedByMovie(
            @PathVariable UUID movieId,
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String roomType
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            MovieShowtimeResponse data = showtimeService.getGroupedShowtimesByMovieAndCinema(
                    movieId, cinemaId, date, roomType
            );

            response.put("message", "Fetched showtimes for movie " + movieId + " successfully");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}