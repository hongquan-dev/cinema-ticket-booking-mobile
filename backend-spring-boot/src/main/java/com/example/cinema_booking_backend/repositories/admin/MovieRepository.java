package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    // JpaRepository already supports Pagination by default
    Page<Movie> findByMovieNameContainingIgnoreCase(String movieName, Pageable pageable);

    // Search by name and sort by release date
    Page<Movie> findByMovieNameContainingIgnoreCaseOrderByReleaseDateDesc(String movieName, Pageable pageable);

    // Find all and sort by release date (for cases where no search string is provided)
    Page<Movie> findAllByOrderByReleaseDateDesc(Pageable pageable);

    List<Movie> findByReleaseDateAfterOrderByReleaseDateAsc(LocalDate date);
}