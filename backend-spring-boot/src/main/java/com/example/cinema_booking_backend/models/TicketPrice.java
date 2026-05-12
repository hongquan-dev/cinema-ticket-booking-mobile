package com.example.cinema_booking_backend.models;

import com.example.cinema_booking_backend.enums.booking.CustomerType;
import com.example.cinema_booking_backend.enums.booking.DayType;
import com.example.cinema_booking_backend.enums.booking.MovieFormat;
import com.example.cinema_booking_backend.enums.booking.SeatType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime; // Use LocalTime for time ranges
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_prices")
public class TicketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    private MovieFormat movieFormat;

    @Enumerated(EnumType.STRING)
    private DayType dayType;

    // --- Time Range Fields ---
    @Column(nullable = false)
    private LocalTime startRange; // e.g., 08:00:00

    @Column(nullable = false)
    private LocalTime endRange;   // e.g., 12:00:00

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public MovieFormat getMovieFormat() {
        return movieFormat;
    }

    public void setMovieFormat(MovieFormat movieFormat) {
        this.movieFormat = movieFormat;
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public LocalTime getStartRange() {
        return startRange;
    }

    public void setStartRange(LocalTime startRange) {
        this.startRange = startRange;
    }

    public LocalTime getEndRange() {
        return endRange;
    }

    public void setEndRange(LocalTime endRange) {
        this.endRange = endRange;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}