package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.models.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema, UUID> {
    // For searching cinemas by name
    Page<Cinema> findByNameContainingIgnoreCase(String name, Pageable pageable);
}