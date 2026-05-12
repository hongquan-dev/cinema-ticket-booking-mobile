package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.dtos.showtime.MovieShowtimeResponse;
import com.example.cinema_booking_backend.dtos.showtime.ShowtimeRequest;
import com.example.cinema_booking_backend.models.Showtime;
import com.example.cinema_booking_backend.services.admin.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    // MÔN KIỂM THỬ
    @PostMapping
    public ResponseEntity<Map<String, Object>> createShowtime(@RequestBody ShowtimeRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Showtime savedShowtime = showtimeService.createShowtime(request);
            response.put("message", "Showtime created successfully!");
            response.put("data", savedShowtime);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Trả về 400 cho các lỗi thiếu dữ liệu, giá vé, trùng lịch...
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (NoSuchElementException e) {
            // Trả về 404 cho lỗi không tìm thấy Phim/Phòng
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getByRoom(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Showtime> showtimePage = showtimeService.getShowtimesByRoom(roomId, page, size);
            if (showtimePage.isEmpty()) {
                response.put("message", "No showtime found");
            } else {
                response.put("message", "Fetched showtime successfully");
            }

            response.put("showtime", showtimePage.getContent());
            response.put("currentPage", showtimePage.getNumber() + 1);
            response.put("totalItems", showtimePage.getTotalElements());
            response.put("totalPages", showtimePage.getTotalPages());
            try {
                response.put("pageSize", showtimePage.getSize());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<Map<String, Object>> getByCinema(
            @PathVariable UUID cinemaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Showtime> showtimePage = showtimeService.getShowtimesByCinema(cinemaId, page, size);

            if (showtimePage.isEmpty()) {
                response.put("message", "No showtimes found for this cinema");
            } else {
                response.put("message", "Fetched cinema showtimes successfully");
            }

            response.put("showtimes", showtimePage.getContent());
            response.put("currentPage", showtimePage.getNumber() + 1);
            response.put("totalItems", showtimePage.getTotalElements());
            response.put("totalPages", showtimePage.getTotalPages());
            response.put("pageSize", showtimePage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            showtimeService.deleteShowtime(id);
            response.put("message", "Showtime deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateShowtime(
            @PathVariable UUID id,
            @RequestBody ShowtimeRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Showtime updatedShowtime = showtimeService.updateShowtime(id, request);
            response.put("message", "Showtime updated successfully!");
            response.put("data", updatedShowtime);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Update error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String movieName,
            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) String cinemaName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Showtime> showtimePage = showtimeService.getAllShowtimes(page, size, movieName, roomName, cinemaName);

            response.put("message", "Fetched showtimes successfully");
            // Match the keys with your React frontend
            response.put("showtimes", showtimePage.getContent());
            response.put("currentPage", showtimePage.getNumber() + 1);
            response.put("totalItems", showtimePage.getTotalElements());
            response.put("totalPages", showtimePage.getTotalPages());
            response.put("pageSize", showtimePage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

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