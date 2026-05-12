package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.models.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {

    // Find all showtimes for a specific room
    Page<Showtime> findByRoomId(UUID roomId, Pageable pageable);

    @Query("SELECT s FROM Showtime s WHERE s.room.cinema.id = :cinemaId")
    Page<Showtime> findByCinemaId(@Param("cinemaId") UUID cinemaId, Pageable pageable);

    // Overlap check logic: (StartA < EndB) AND (EndA > StartB)
    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
            "AND ((s.startTime < :endTime) AND (s.endTime > :startTime))")
    List<Showtime> findOverlappingShowtimes(
            @Param("roomId") UUID roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
            "AND s.id <> :excludeId " + // Exclude current showtime ID
            "AND ((s.startTime < :endTime) AND (s.endTime > :startTime))")
    List<Showtime> findOverlappingShowtimesExcludingSelf(
            @Param("roomId") UUID roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") UUID excludeId
    );

    @Query("SELECT s FROM Showtime s JOIN s.movie m JOIN s.room r JOIN r.cinema c " +
            "WHERE (:movieName IS NULL OR LOWER(CAST(m.movieName as text)) LIKE LOWER(CONCAT('%', CAST(:movieName as text), '%'))) " +
            "AND (:roomName IS NULL OR LOWER(CAST(r.name as text)) LIKE LOWER(CONCAT('%', CAST(:roomName as text), '%'))) " +
            "AND (:cinemaName IS NULL OR LOWER(CAST(c.name as text)) LIKE LOWER(CONCAT('%', CAST(:cinemaName as text), '%')))")
    Page<Showtime> findAllWithFilters(
            @Param("movieName") String movieName,
            @Param("roomName") String roomName,
            @Param("cinemaName") String cinemaName,
            Pageable pageable
    );

    List<Showtime> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);

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