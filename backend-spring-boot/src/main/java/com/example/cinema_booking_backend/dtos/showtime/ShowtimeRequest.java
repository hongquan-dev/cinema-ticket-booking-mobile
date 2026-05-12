package com.example.cinema_booking_backend.dtos.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ShowtimeRequest {
    private UUID movieId;
    private UUID roomId;
    private LocalDateTime startTime;
    private BigDecimal basePrice;

    public ShowtimeRequest(UUID movieId, UUID roomId, LocalDateTime startTime, BigDecimal basePrice) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.basePrice = basePrice;
    }

    public UUID getMovieId() {
        return movieId;
    }

    public void setMovieId(UUID movieId) {
        this.movieId = movieId;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}
