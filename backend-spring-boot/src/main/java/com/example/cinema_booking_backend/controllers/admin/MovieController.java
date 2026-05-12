package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.dtos.movie.MovieListResponse;
import com.example.cinema_booking_backend.models.Movie;
import com.example.cinema_booking_backend.services.admin.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // Change to accept Multipart Form Data
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> createMovie(
            @RequestPart("movie") String movieJson, // Receive Movie info as JSON string
            @RequestPart(value = "file", required = false) MultipartFile file // Receive Image file
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Convert JSON string to Movie Object
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Support for LocalDate/LocalDateTime
            Movie movie = objectMapper.readValue(movieJson, Movie.class);

            // Save via service (Service will handle Cloudinary upload)
            Movie savedMovie = movieService.saveMovieWithPoster(movie, file);

            response.put("message", "Movie created successfully with poster!");
            response.put("data", savedMovie);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            // Add optional search parameter
            @RequestParam(required = false) String search
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Pass the search string to the service layer
            Page<MovieListResponse> moviePage = movieService.getAllMoviesPaged(page, size, search);

            if (moviePage.isEmpty()) {
                response.put("message", "No movies with name: " + search + " were found");
            } else {
                response.put("message", "Fetched movies successfully");
            }
            response.put("movies", moviePage.getContent());
            response.put("currentPage", moviePage.getNumber() + 1);
            response.put("totalItems", moviePage.getTotalElements());
            response.put("totalPages", moviePage.getTotalPages());
            response.put("pageSize", moviePage.getSize());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Log error and return 400
            response.put("message", "Invalid parameters: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Log unexpected errors and return 500
            response.put("message", "Could not fetch movie list: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMovieById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Movie movie = movieService.getMovieById(id);

            response.put("message", "Fetched movie details successfully");
            response.put("data", movie);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> updateMovie(
            @PathVariable UUID id,
            @RequestPart("movie") String movieJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (id == null) {
                response.put("message", "Movie ID is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Movie movieDetails = objectMapper.readValue(movieJson, Movie.class);

            Movie updatedMovie = movieService.updateMovie(id, movieDetails, file);

            response.put("message", "Movie updated successfully!");
            response.put("data", updatedMovie);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Update error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMovie(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (id == null) {
                response.put("message", "Movie ID is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            movieService.deleteMovie(id);

            response.put("message", "Movie deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

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