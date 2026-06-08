package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.enums.common.TicketStatus;
import com.example.cinema_booking_backend.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    @Query("SELECT t.seat.id FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.status = 'BOOKED'")
    List<UUID> findReservedSeatIdsByShowtimeId(@Param("showtimeId") UUID showtimeId);

    boolean existsByShowtimeIdAndSeatIdAndStatus(UUID showtimeId, UUID seatId, TicketStatus status);

    Page<Ticket> findByUserId(UUID userId, Pageable pageable);

    Optional<Ticket> findByIdAndUserId(UUID ticketId, UUID userId);

    boolean existsByOrderCode(String orderCode);

    List<Ticket> findByOrderCode(String orderCode);
}
