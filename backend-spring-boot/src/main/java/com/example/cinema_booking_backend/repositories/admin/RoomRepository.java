package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.models.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    // Find rooms by cinema ID with pagination
    Page<Room> findByCinemaId(UUID cinemaId, Pageable pageable);

    // Search rooms by name within a specific cinema
    Page<Room> findByCinemaIdAndNameContainingIgnoreCase(UUID cinemaId, String name, Pageable pageable);
}