package com.example.cinema_booking_backend.dtos.ticket;

import com.example.cinema_booking_backend.enums.ticket.TicketStatus;
import com.example.cinema_booking_backend.models.Showtime;
import com.example.cinema_booking_backend.models.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResponse {
    private String orderCode;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal finalTotalAmount;


    // User info
    private String customerName;
    private String userName;
    private String email;
    private Boolean verify;
    private String phoneNumber;

    // Showtime & Movie info
    private String movieName;
    private String theaterName;
    private String roomName;
    private String roomType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Seats list
    private List<SeatDetailDto> seats;

    @Data
    public static class SeatDetailDto {
        private String seatNumber;
        private String seatType;
        private BigDecimal price;
    }
}