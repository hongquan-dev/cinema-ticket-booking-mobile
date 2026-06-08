package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.enums.booking.CustomerType;
import com.example.cinema_booking_backend.enums.booking.DayType;
import com.example.cinema_booking_backend.enums.booking.MovieFormat;
import com.example.cinema_booking_backend.enums.booking.SeatType;
import com.example.cinema_booking_backend.entity.TicketPrice;
import com.example.cinema_booking_backend.services.TicketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ticket-prices")
public class TicketPriceController {

    @Autowired
    private TicketPriceService ticketPriceService;

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> getPrice(
            @RequestParam MovieFormat movieFormat,
            @RequestParam SeatType seatType,
            @RequestParam DayType dayType,
            @RequestParam CustomerType customerType,
            @RequestParam String startTime
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