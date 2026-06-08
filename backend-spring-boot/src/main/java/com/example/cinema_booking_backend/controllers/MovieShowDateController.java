package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.entity.MovieShowDate;
import com.example.cinema_booking_backend.services.MovieShowDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movie-show-dates")
public class MovieShowDateController {

    @Autowired
    private MovieShowDateService movieShowDateService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MovieShowDate> dates = movieShowDateService.getAllShowDates();
            response.put("message", "Fetched all show dates successfully");
            response.put("data", dates);
            response.put("total", dates.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}