package com.example.cinema_booking_backend.repositories.user;

import com.example.cinema_booking_backend.dtos.ticket.OrderListResponse;
import com.example.cinema_booking_backend.enums.ticket.TicketStatus;
import com.example.cinema_booking_backend.models.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    // Get all seat IDs that are already booked for a specific showtime
    @Query("SELECT t.seat.id FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.status = 'BOOKED'")
    List<UUID> findReservedSeatIdsByShowtimeId(@Param("showtimeId") UUID showtimeId);

    // Check if a seat is already booked for a specific showtime
    boolean existsByShowtimeIdAndSeatIdAndStatus(UUID showtimeId, UUID seatId, TicketStatus status);

    // Find all tickets for a specific user, ordered by booking time (newest first)
    Page<Ticket> findByUserId(UUID userId, Pageable pageable);

    // Find tickets by ID and UserID (for security when deleting)
    Optional<Ticket> findByIdAndUserId(UUID ticketId, UUID userId);

    // Check if orderCode exists
    boolean existsByOrderCode(String orderCode);

    @Query("SELECT new com.example.cinema_booking_backend.dtos.ticket.OrderListResponse(" +
            "t.orderCode, " +
            "t.showtime, " +
            "SUM(t.finalPrice), " +
            "t.status, " +
            "MAX(t.createdAt)) " +
            "FROM Ticket t " +
            "JOIN t.showtime s " +
            "JOIN s.movie m " +
            // Thay đoạn filter Date bằng đoạn này:
            "WHERE (COALESCE(:date, NULL) IS NULL OR CAST(t.createdAt AS date) = :date) " +
            "AND (:movieTitle IS NULL OR LOWER(CAST(m.movieName AS text)) LIKE LOWER(CONCAT('%', CAST(:movieTitle AS text), '%'))) " +
            "AND (:orderCode IS NULL OR LOWER(CAST(t.orderCode AS text)) LIKE LOWER(CONCAT('%', CAST(:orderCode AS text), '%'))) " +
            "GROUP BY t.orderCode, t.showtime, t.status " +
            "ORDER BY MAX(t.createdAt) DESC")
    Page<OrderListResponse> findAllGroupedWithFilters(
            @Param("date") java.time.LocalDate date,
            @Param("movieTitle") String movieTitle,
            @Param("orderCode") String orderCode,
            Pageable pageable);

    List<Ticket> findByOrderCode(String orderCode);
}
