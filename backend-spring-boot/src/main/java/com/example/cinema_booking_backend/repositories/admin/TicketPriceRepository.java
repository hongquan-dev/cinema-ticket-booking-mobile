package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.enums.booking.CustomerType;
import com.example.cinema_booking_backend.enums.booking.DayType;
import com.example.cinema_booking_backend.enums.booking.MovieFormat;
import com.example.cinema_booking_backend.enums.booking.SeatType;
import com.example.cinema_booking_backend.models.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, UUID> {

    @Query("SELECT tp FROM TicketPrice tp WHERE " +
            "tp.movieFormat = :movieFormat AND " +
            "tp.seatType = :seatType AND " +
            "tp.dayType = :dayType AND " +
            "tp.customerType = :customerType AND " +
            ":checkTime BETWEEN tp.startRange AND tp.endRange")
    Optional<TicketPrice> findSpecificPrice(
            @Param("movieFormat") MovieFormat movieFormat,
            @Param("seatType") SeatType seatType,
            @Param("dayType") DayType dayType,
            @Param("customerType") CustomerType customerType,
            @Param("checkTime") LocalTime checkTime
    );
}