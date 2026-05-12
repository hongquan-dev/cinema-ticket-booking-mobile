package com.example.cinema_booking_backend.dtos.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.cinema_booking_backend.enums.ticket.TicketStatus;
import com.example.cinema_booking_backend.models.Seat;
import com.example.cinema_booking_backend.models.Showtime;
import lombok.Data;

@Data
public class TicketListResponse {
    private String orderCode;
    private Showtime showtime;
    private Seat seat;
    private BigDecimal finalPrice;
    private TicketStatus status;
    private LocalDateTime createAt;

    public TicketListResponse(String orderCode, Showtime showtime, Seat seat,BigDecimal finalPrice, TicketStatus status, LocalDateTime createAt) {
        this.orderCode = orderCode;
        this.showtime = showtime;
        this.seat = seat;
        this.finalPrice = finalPrice;
        this.status = status;
        this.createAt = createAt;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}