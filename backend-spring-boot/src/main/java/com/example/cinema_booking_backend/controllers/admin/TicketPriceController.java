package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.enums.booking.CustomerType;
import com.example.cinema_booking_backend.enums.booking.DayType;
import com.example.cinema_booking_backend.enums.booking.MovieFormat;
import com.example.cinema_booking_backend.enums.booking.SeatType;
import com.example.cinema_booking_backend.models.TicketPrice;
import com.example.cinema_booking_backend.services.admin.TicketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ticket-prices")
public class TicketPriceController {

    @Autowired
    private TicketPriceService ticketPriceService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTicketPrice(@RequestBody TicketPrice ticketPrice) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Basic validation
            if (ticketPrice.getTicketPrice() == null) {
                response.put("message", "Price value is required");
                return ResponseEntity.badRequest().body(response);
            }

            TicketPrice saved = ticketPriceService.createTicketPrice(ticketPrice);
            response.put("message", "Ticket price configuration added!");
            response.put("data", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTicketPrices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<TicketPrice> pricePage = ticketPriceService.getAllTicketPricesPaged(page, size);

            response.put("message", "Fetched prices successfully");
            response.put("data", pricePage.getContent());
            response.put("totalItems", pricePage.getTotalElements());
            response.put("totalPages", pricePage.getTotalPages());
            response.put("currentPage", pricePage.getNumber() + 1);
            response.put("pageSize", pricePage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTicketPrice(
            @PathVariable UUID id,
            @RequestBody TicketPrice details
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            TicketPrice updated = ticketPriceService.updateTicketPrice(id, details);
            response.put("message", "Updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTicketPrice(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ticketPriceService.deleteTicketPrice(id);
            response.put("message", "Deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Delete failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> getPrice(
            @RequestParam MovieFormat movieFormat,
            @RequestParam SeatType seatType,
            @RequestParam DayType dayType,
            @RequestParam CustomerType customerType,
            @RequestParam String startTime // Format: "HH:mm:ss"
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalTime time = LocalTime.parse(startTime);
            TicketPrice priceConfig = ticketPriceService.calculatePrice(
                    movieFormat, seatType, dayType, customerType, time);

            response.put("message", "Price calculated successfully");
            response.put("ticketPrice", priceConfig.getTicketPrice());
            response.put("data", priceConfig);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error calculating price: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}