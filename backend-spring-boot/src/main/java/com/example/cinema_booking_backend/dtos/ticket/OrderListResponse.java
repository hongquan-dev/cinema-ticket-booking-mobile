package com.example.cinema_booking_backend.dtos.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.cinema_booking_backend.enums.ticket.TicketStatus;
import com.example.cinema_booking_backend.models.Showtime;
import lombok.Data;

@Data
public class OrderListResponse {
    private String orderCode;
    private Showtime showtime;
    private BigDecimal finalPrice;
    private TicketStatus status;
    private LocalDateTime createAt;

    public OrderListResponse(String orderCode, Showtime showtime, BigDecimal finalPrice, TicketStatus status, LocalDateTime createAt) {
        this.orderCode = orderCode;
        this.showtime = showtime;
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