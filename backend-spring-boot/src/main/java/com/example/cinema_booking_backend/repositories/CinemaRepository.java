package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema, UUID> {
}