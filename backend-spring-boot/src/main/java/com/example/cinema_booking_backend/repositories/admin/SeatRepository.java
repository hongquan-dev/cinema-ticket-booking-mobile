package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    // Delete existing seats if admin wants to recreate layout
    void deleteByRoomId(UUID roomId);

    // Find all seats in a room
    List<Seat> findByRoomIdOrderByRowIndexAscColIndexAsc(UUID roomId);

    List<Seat> findByRoomId(UUID id);
}