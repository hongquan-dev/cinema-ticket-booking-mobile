package com.example.cinema_booking_backend.services.admin;

import com.example.cinema_booking_backend.enums.booking.CustomerType;
import com.example.cinema_booking_backend.enums.booking.DayType;
import com.example.cinema_booking_backend.enums.booking.MovieFormat;
import com.example.cinema_booking_backend.enums.booking.SeatType;
import com.example.cinema_booking_backend.models.TicketPrice;
import com.example.cinema_booking_backend.repositories.admin.TicketPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

@Service
public class TicketPriceService {

    @Autowired
    private TicketPriceRepository ticketPriceRepository;

    @Transactional(rollbackFor = Exception.class)
    public TicketPrice createTicketPrice(TicketPrice ticketPrice) {
        return ticketPriceRepository.save(ticketPrice);
    }

    public Page<TicketPrice> getAllTicketPricesPaged(int page, int size) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ticketPrice").ascending());
        return ticketPriceRepository.findAll(pageable);
    }

    public TicketPrice getTicketPriceById(UUID id) {
        return ticketPriceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket price config not found: " + id));
    }

    @Transactional(rollbackFor = Exception.class)
    public TicketPrice updateTicketPrice(UUID id, TicketPrice details) {
        TicketPrice existing = getTicketPriceById(id);

        existing.setSeatType(details.getSeatType());
        existing.setMovieFormat(details.getMovieFormat());
        existing.setDayType(details.getDayType());
        existing.setStartRange(details.getStartRange());
        existing.setEndRange(details.getEndRange());
        existing.setCustomerType(details.getCustomerType());
        existing.setTicketPrice(details.getTicketPrice());
        existing.setDescription(details.getDescription());

        return ticketPriceRepository.save(existing);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTicketPrice(UUID id) {
        TicketPrice existing = getTicketPriceById(id);
        ticketPriceRepository.delete(existing);
    }

    public TicketPrice calculatePrice(MovieFormat format, SeatType seat, DayType day, CustomerType customer, LocalTime time) {
        return ticketPriceRepository.findSpecificPrice(format, seat, day, customer, time)
                .orElseThrow(() -> new RuntimeException("No ticket price configuration found for the selected criteria."));
    }
}