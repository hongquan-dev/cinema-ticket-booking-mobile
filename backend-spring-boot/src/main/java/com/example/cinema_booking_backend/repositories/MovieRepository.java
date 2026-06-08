package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    List<Movie> findByReleaseDateAfterOrderByReleaseDateAsc(LocalDate date);
}