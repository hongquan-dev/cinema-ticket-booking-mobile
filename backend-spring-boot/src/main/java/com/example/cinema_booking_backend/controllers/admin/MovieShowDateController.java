package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.models.MovieShowDate;
import com.example.cinema_booking_backend.services.admin.MovieShowDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/movie-show-dates")
public class MovieShowDateController {

    @Autowired
    private MovieShowDateService movieShowDateService;

    // Create
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody MovieShowDate showDate) {
        Map<String, Object> response = new HashMap<>();
        try {
            MovieShowDate savedDate = movieShowDateService.createShowDate(showDate);
            response.put("message", "Show date created successfully");
            response.put("data", savedDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get all
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

    // Update by ID
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @RequestBody MovieShowDate showDateDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            MovieShowDate updatedDate = movieShowDateService.updateShowDate(id, showDateDetails);
            response.put("message", "Show date updated successfully");
            response.put("data", updatedDate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            movieShowDateService.deleteShowDate(id);
            response.put("message", "Show date deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete all
    @DeleteMapping("/all")
    public ResponseEntity<Map<String, Object>> deleteAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            movieShowDateService.deleteAllShowDates();
            response.put("message", "All show dates deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}