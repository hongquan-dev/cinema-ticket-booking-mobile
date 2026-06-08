package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.dto.ticket.*;
import com.example.cinema_booking_backend.enums.common.TicketStatus;
import com.example.cinema_booking_backend.entity.Seat;
import com.example.cinema_booking_backend.entity.Showtime;
import com.example.cinema_booking_backend.entity.Ticket;
import com.example.cinema_booking_backend.entity.User;
import com.example.cinema_booking_backend.repositories.SeatRepository;
import com.example.cinema_booking_backend.repositories.ShowtimeRepository;
import com.example.cinema_booking_backend.repositories.TicketRepository;
import com.example.cinema_booking_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.security.SecureRandom;

@Service
public class BookingService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    private final SecureRandom random = new SecureRandom();

    public List<SeatWithStatusResponse> getSeatsLayoutByShowtime(UUID showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        List<Seat> allSeats = seatRepository.findByRoomId(showtime.getRoom().getId());

        List<UUID> reservedSeatIds = ticketRepository.findReservedSeatIdsByShowtimeId(showtimeId);

        return allSeats.stream().map(seat -> {
            SeatWithStatusResponse dto = new SeatWithStatusResponse();
            dto.setId(seat.getId());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setSeatType(seat.getSeatType());
            dto.setRowIndex(seat.getRowIndex());
            dto.setColIndex(seat.getColIndex());
            dto.setColorCode(seat.getColorCode());
            dto.setPrice(showtime.getBasePrice());
            dto.setReserved(reservedSeatIds.contains(seat.getId()) || !seat.getIsActive());

            return dto;
        }).collect(Collectors.toList());
    }

    private String generateUniqueOrderCode() {
        String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String orderCode;
        boolean exists;

        do {
            int randomNumber = random.nextInt(900) + 100;
            orderCode = prefix + randomNumber;

            exists = ticketRepository.existsByOrderCode(orderCode);
        } while (exists);

        return orderCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Ticket> createBooking(BookingRequest request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String sharedOrderCode = generateUniqueOrderCode();

        List<Ticket> savedTickets = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {
            if (ticketRepository.existsByShowtimeIdAndSeatIdAndStatus(request.getShowtimeId(), seatId, TicketStatus.BOOKED)) {
                throw new RuntimeException("Seat " + seatId + " is already occupied!");
            }

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

            Ticket ticket = new Ticket();
            ticket.setOrderCode(sharedOrderCode);
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            ticket.setUser(user);
            ticket.setStatus(TicketStatus.BOOKED);
            BigDecimal finalPrice = determinePrice(seat, request, showtime);
            ticket.setFinalPrice(finalPrice);

            savedTickets.add(ticketRepository.save(ticket));
        }

        return savedTickets;
    }

    private BigDecimal determinePrice(Seat seat, BookingRequest request, Showtime showtime) {
        String type = seat.getSeatType().toUpperCase();

        switch (type) {
            case "VIP":
                return request.getPriceVip() != null ? request.getPriceVip() : showtime.getBasePrice();
            case "SWEETBOX":
                return request.getPriceSweetbox() != null ? request.getPriceSweetbox() : showtime.getBasePrice();
            case "STANDARD":
            default:
                return request.getPriceStandard() != null ? request.getPriceStandard() : showtime.getBasePrice();
        }
    }

    public Page<TicketListResponse> getTicketsByUserId(UUID userId, int page, int size) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Page<Ticket> ticketPage = ticketRepository.findByUserId(userId, pageable);

        return ticketPage.map(ticket -> new TicketListResponse(
                ticket.getOrderCode(),
                ticket.getShowtime(),
                ticket.getSeat(),
                ticket.getFinalPrice(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(String orderCode) {
        List<Ticket> tickets = ticketRepository.findByOrderCode(orderCode);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found with order code: " + orderCode);
        }

        Ticket firstTicket = tickets.get(0);
        if (firstTicket.getStatus() == TicketStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled.");
        }

        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.CANCELLED);
        }

        ticketRepository.saveAll(tickets);
    }
}