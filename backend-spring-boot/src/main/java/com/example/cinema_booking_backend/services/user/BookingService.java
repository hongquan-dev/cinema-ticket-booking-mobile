package com.example.cinema_booking_backend.services.user;

import com.example.cinema_booking_backend.dtos.ticket.*;
import com.example.cinema_booking_backend.enums.ticket.TicketStatus;
import com.example.cinema_booking_backend.models.Seat;
import com.example.cinema_booking_backend.models.Showtime;
import com.example.cinema_booking_backend.models.Ticket;
import com.example.cinema_booking_backend.models.User;
import com.example.cinema_booking_backend.repositories.admin.SeatRepository;
import com.example.cinema_booking_backend.repositories.admin.ShowtimeRepository;
import com.example.cinema_booking_backend.repositories.user.TicketRepository;
import com.example.cinema_booking_backend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        // 1. Fetch Showtime
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // 2. Fetch all seats for the room of this showtime
        List<Seat> allSeats = seatRepository.findByRoomId(showtime.getRoom().getId());

        // 3. Get currently reserved seat IDs
        List<UUID> reservedSeatIds = ticketRepository.findReservedSeatIdsByShowtimeId(showtimeId);

        // 4. Map to DTO with status and calculated price
        return allSeats.stream().map(seat -> {
            SeatWithStatusResponse dto = new SeatWithStatusResponse();
            dto.setId(seat.getId());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setSeatType(seat.getSeatType());
            dto.setRowIndex(seat.getRowIndex());
            dto.setColIndex(seat.getColIndex());
            dto.setColorCode(seat.getColorCode());

            // Final Price = Base Price + Extra Price (VIP/Sweetbox)
            dto.setPrice(showtime.getBasePrice());

            // Check if this seat is in the reserved list
            dto.setReserved(reservedSeatIds.contains(seat.getId()) || !seat.getIsActive());

            return dto;
        }).collect(Collectors.toList());
    }

    private String generateUniqueOrderCode() {
        String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String orderCode;
        boolean exists;

        do {
            // Generate 3 random digits
            int randomNumber = random.nextInt(900) + 100; // Range 100-999
            orderCode = prefix + randomNumber;

            // Check if this code already exists in DB
            exists = ticketRepository.existsByOrderCode(orderCode);
        } while (exists);

        return orderCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Ticket> createBooking(BookingRequest request) {
        // 1. Validate Showtime and User
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Generate a single Order Code for the entire request
        String sharedOrderCode = generateUniqueOrderCode();

        List<Ticket> savedTickets = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {
            // 3. Check if seat is already booked
            if (ticketRepository.existsByShowtimeIdAndSeatIdAndStatus(request.getShowtimeId(), seatId, TicketStatus.BOOKED)) {
                throw new RuntimeException("Seat " + seatId + " is already occupied!");
            }

            // 4. Fetch Seat details
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

            // 5. Create Ticket with shared info
            Ticket ticket = new Ticket();
            ticket.setOrderCode(sharedOrderCode); // Shared code for all tickets in this transaction
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

    public Page<OrderListResponse> getAllTicketsFiltered(String dateStr, String movieTitle, String orderCode, int page, int size) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDate filterDate = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                filterDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                throw new RuntimeException("Invalid date format. Please use yyyy-MM-dd");
            }
        }

        String movieFilter = (movieTitle != null && !movieTitle.trim().isEmpty()) ? movieTitle.trim() : null;
        String codeFilter = (orderCode != null && !orderCode.trim().isEmpty()) ? orderCode.trim() : null;

        return ticketRepository.findAllGroupedWithFilters(filterDate, movieFilter, codeFilter, pageable);
    }

    public OrderDetailResponse getOrderDetailByCode(String orderCode) {
        // 1. Fetch all tickets with the same orderCode
        List<Ticket> tickets = ticketRepository.findByOrderCode(orderCode);

        if (tickets.isEmpty()) {
            throw new RuntimeException("Order not found with code: " + orderCode);
        }

        // 2. Take common info from the first ticket (Showtime, User, Status...)
        Ticket firstTicket = tickets.get(0);
        Showtime showtime = firstTicket.getShowtime();
        User user = firstTicket.getUser();

        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderCode(firstTicket.getOrderCode());
        response.setStatus(firstTicket.getStatus());
        response.setCreatedAt(firstTicket.getCreatedAt());

        // User Info
        response.setCustomerName(user.getFullName());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setVerify(user.isVerified());
        response.setPhoneNumber(user.getPhoneNumber());

        // Showtime/Movie Info
        response.setMovieName(showtime.getMovie().getMovieName());
        response.setTheaterName(showtime.getRoom().getCinema().getName());
        response.setRoomName(showtime.getRoom().getName());
        response.setRoomType(showtime.getRoom().getRoomType());
        response.setStartTime(showtime.getStartTime());
        response.setEndTime(showtime.getEndTime());

        // 3. Process list of seats and Calculate total amount
        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetailResponse.SeatDetailDto> seatDetails = new ArrayList<>();

        for (Ticket t : tickets) {
            OrderDetailResponse.SeatDetailDto seatDto = new OrderDetailResponse.SeatDetailDto();
            seatDto.setSeatNumber(t.getSeat().getSeatNumber());
            seatDto.setSeatType(t.getSeat().getSeatType());
            seatDto.setPrice(t.getFinalPrice());

            seatDetails.add(seatDto);
            total = total.add(t.getFinalPrice());
        }

        response.setSeats(seatDetails);
        response.setTotalAmount(total);
        response.setDiscount(new BigDecimal(0));
        response.setFinalTotalAmount(total);

        return response;
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

        // Sort by 'createdAt' descending to show the newest bookings at the top
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 1. Fetch the Page of Ticket entities
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

        // If showtime had started, not allow cancel
        // if (firstTicket.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
        //     throw new RuntimeException("Cannot cancel tickets for a showtime that has already started.");
        // }

        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.CANCELLED);
        }

        ticketRepository.saveAll(tickets);
    }
}