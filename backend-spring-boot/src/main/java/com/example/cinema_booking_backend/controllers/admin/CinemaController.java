package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.models.Cinema;
import com.example.cinema_booking_backend.services.admin.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cinemas")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCinema(@RequestBody Cinema cinema) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validation: Ensure name and address are not empty
            if (cinema.getName() == null || cinema.getName().trim().isEmpty()) {
                response.put("message", "Cinema name is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (cinema.getAddress() == null || cinema.getAddress().trim().isEmpty()) {
                response.put("message", "Address is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Cinema savedCinema = cinemaService.createCinema(cinema);

            response.put("message", "Cinema created successfully!");
            response.put("data", savedCinema);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCinemas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, // Default set to 10 records per page
            @RequestParam(required = false) String search
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            org.springframework.data.domain.Page<Cinema> cinemaPage = cinemaService.getAllCinemasPaged(page, size, search);

            if (cinemaPage.isEmpty()) {
                response.put("message", "No cinemas found");
            } else {
                response.put("message", "Fetched cinemas successfully");
            }

            response.put("cinemas", cinemaPage.getContent());
            response.put("currentPage", cinemaPage.getNumber() + 1);
            response.put("totalItems", cinemaPage.getTotalElements());
            response.put("totalPages", cinemaPage.getTotalPages());

            try {
                response.put("pageSize", cinemaPage.getSize());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Could not fetch cinema list: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCinemaById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cinema cinema = cinemaService.getCinemaById(id);

            response.put("message", "Fetched cinema details successfully");
            response.put("data", cinema);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCinema(
            @PathVariable UUID id,
            @RequestBody Cinema cinemaDetails
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (id == null) {
                response.put("message", "Cinema ID is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Cinema updatedCinema = cinemaService.updateCinema(id, cinemaDetails);

            response.put("message", "Cinema updated successfully!");
            response.put("data", updatedCinema);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "Update error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCinema(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (id == null) {
                response.put("message", "Cinema ID is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            cinemaService.deleteCinema(id);

            response.put("message", "Cinema deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}