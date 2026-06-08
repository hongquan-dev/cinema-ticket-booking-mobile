package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.dto.ticket.*;
import com.example.cinema_booking_backend.entity.Ticket;
import com.example.cinema_booking_backend.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/seat-layout/{showtimeId}")
    public ResponseEntity<Map<String, Object>> getRoomLayout(@PathVariable UUID showtimeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SeatWithStatusResponse> seats = bookingService.getSeatsLayoutByShowtime(showtimeId);
            response.put("message", "Fetched seat layout successfully");
            response.put("seats", seats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping("/book")
    public ResponseEntity<Map<String, Object>> bookTickets(@RequestBody BookingRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ticket> tickets = bookingService.createBooking(request);
            response.put("message", "Booking successful!");
            response.put("tickets", tickets);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Booking failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/my-tickets/{userId}")
    public ResponseEntity<Map<String, Object>> getMyTickets(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            Page<TicketListResponse> result = bookingService.getTicketsByUserId(userId, page, size);

            response.put("message", "Fetched user ticket history successfully");
            response.put("tickets", result.getContent());
            response.put("currentPage", result.getNumber() + 1);
            response.put("totalItems", result.getTotalElements());
            response.put("totalPages", result.getTotalPages());
            response.put("pageSize", result.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/cancel/{orderCode}")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String orderCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            bookingService.cancelBooking(orderCode);
            response.put("message", "Order cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Cancel failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}