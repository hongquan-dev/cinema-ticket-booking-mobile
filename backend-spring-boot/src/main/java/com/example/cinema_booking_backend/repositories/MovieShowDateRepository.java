package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.entity.MovieShowDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieShowDateRepository extends JpaRepository<MovieShowDate, UUID> {
}