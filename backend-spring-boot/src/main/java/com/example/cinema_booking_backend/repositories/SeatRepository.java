package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByRoomId(UUID id);
}