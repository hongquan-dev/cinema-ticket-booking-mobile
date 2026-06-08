package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {
    @Query("SELECT s FROM Showtime s WHERE s.room.cinema.id = :cinemaId " +
            "AND s.startTime BETWEEN :start AND :end")
    List<Showtime> findAllByCinemaAndDate(
            @Param("cinemaId") UUID cinemaId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId " +
            "AND s.room.cinema.id = :cinemaId " +
            "AND s.startTime BETWEEN :start AND :end")
    List<Showtime> findAllByMovieCinemaAndDate(
            @Param("movieId") UUID movieId,
            @Param("cinemaId") UUID cinemaId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}