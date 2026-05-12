package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.models.MovieShowDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieShowDateRepository extends JpaRepository<MovieShowDate, UUID> {
    // Basic CRUD operations are inherited from JpaRepository
    Optional<MovieShowDate> findByShowDate(LocalDate showDate);
}