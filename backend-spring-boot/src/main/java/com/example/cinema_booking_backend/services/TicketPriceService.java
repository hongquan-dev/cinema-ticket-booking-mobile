package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.enums.booking.CustomerType;
import com.example.cinema_booking_backend.enums.booking.DayType;
import com.example.cinema_booking_backend.enums.booking.MovieFormat;
import com.example.cinema_booking_backend.enums.booking.SeatType;
import com.example.cinema_booking_backend.entity.TicketPrice;
import com.example.cinema_booking_backend.repositories.TicketPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;

@Service
public class TicketPriceService {

    @Autowired
    private TicketPriceRepository ticketPriceRepository;

    public TicketPrice calculatePrice(MovieFormat format, SeatType seat, DayType day, CustomerType customer, LocalTime time) {
        return ticketPriceRepository.findSpecificPrice(format, seat, day, customer, time)
                .orElseThrow(() -> new RuntimeException("No ticket price configuration found for the selected criteria"));
    }
}