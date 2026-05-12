package com.example.cinema_booking_backend.enums.ticket;

public enum TicketStatus {
    PENDING,   // Ticket created but not yet paid
    BOOKED,    // Payment successful
    CANCELLED  // Cancelled by user or system timeout
}
