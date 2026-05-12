package com.example.cinema_booking_backend.dtos.showtime;

import com.example.cinema_booking_backend.models.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieShowtimeResponse {
    private Movie movie;
    private String roomType;
    private List<ShowtimeInfo> showtimes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShowtimeInfo {
        private UUID id;
        private LocalDateTime startTime;
        private String format;
        private String roomName;
        private BigDecimal basePrice;
    }
}