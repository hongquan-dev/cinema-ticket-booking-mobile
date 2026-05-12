package com.example.cinema_booking_backend.dtos.ticket;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class BookingRequest {
    private UUID showtimeId;
    private List<UUID> seatIds;
    private UUID userId;
    private BigDecimal priceStandard;
    private BigDecimal priceVip;
    private BigDecimal priceSweetbox;

    public UUID getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(UUID showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<UUID> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<UUID> seatIds) {
        this.seatIds = seatIds;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getPriceStandard() {
        return priceStandard;
    }

    public void setPriceStandard(BigDecimal priceStandard) {
        this.priceStandard = priceStandard;
    }

    public BigDecimal getPriceVip() {
        return priceVip;
    }

    public void setPriceVip(BigDecimal priceVip) {
        this.priceVip = priceVip;
    }

    public BigDecimal getPriceSweetbox() {
        return priceSweetbox;
    }

    public void setPriceSweetbox(BigDecimal priceSweetbox) {
        this.priceSweetbox = priceSweetbox;
    }
}