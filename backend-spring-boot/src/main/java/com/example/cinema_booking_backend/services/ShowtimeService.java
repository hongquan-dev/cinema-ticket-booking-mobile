package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.dto.showtime.MovieShowtimeResponse;
import com.example.cinema_booking_backend.entity.Movie;
import com.example.cinema_booking_backend.entity.Showtime;
import com.example.cinema_booking_backend.repositories.CinemaRepository;
import com.example.cinema_booking_backend.repositories.MovieRepository;
import com.example.cinema_booking_backend.repositories.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    public Showtime getShowtimeById(UUID id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));
    }

    public List<MovieShowtimeResponse> getGroupedShowtimesByCinemaAndDate(UUID cinemaId, LocalDate date) {
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new RuntimeException("Cinema not found with ID: " + cinemaId);
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Showtime> allShowtimes = showtimeRepository.findAllByCinemaAndDate(cinemaId, startOfDay, endOfDay);

        if (allShowtimes.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Movie, Map<String, List<Showtime>>> groupedByMovieAndType = allShowtimes.stream()
                .collect(Collectors.groupingBy(
                        Showtime::getMovie,
                        Collectors.groupingBy(s -> s.getRoom().getRoomType())
                ));

        List<MovieShowtimeResponse> finalResponse = new ArrayList<>();

        groupedByMovieAndType.forEach((movie, typeMap) -> {
            typeMap.forEach((roomType, showtimes) -> {
                MovieShowtimeResponse response = new MovieShowtimeResponse();
                response.setMovie(movie);

                response.setRoomType(roomType);
                List<MovieShowtimeResponse.ShowtimeInfo> infoList = showtimes.stream()
                        .sorted(Comparator.comparing(Showtime::getStartTime))
                        .map(s -> new MovieShowtimeResponse.ShowtimeInfo(
                                s.getId(),
                                s.getStartTime(),
                                s.getFormat(),
                                s.getRoom().getName(),
                                s.getBasePrice()
                        ))
                        .collect(Collectors.toList());

                response.setShowtimes(infoList);
                finalResponse.add(response);
            });
        });

        return finalResponse.stream()
                .sorted(Comparator.comparing((MovieShowtimeResponse r) -> r.getMovie().getMovieName())
                        .thenComparing(MovieShowtimeResponse::getRoomType))
                .collect(Collectors.toList());
    }

    public MovieShowtimeResponse getGroupedShowtimesByMovieAndCinema(UUID movieId, UUID cinemaId, LocalDate date, String roomType) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new RuntimeException("Cinema not found");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Showtime> showtimes = showtimeRepository.findAllByMovieCinemaAndDate(movieId, cinemaId, startOfDay, endOfDay);

        List<MovieShowtimeResponse.ShowtimeInfo> infoList = showtimes.stream()
                .filter(s -> s.getRoom().getRoomType().equalsIgnoreCase(roomType))
                .sorted(Comparator.comparing(Showtime::getStartTime))
                .map(s -> new MovieShowtimeResponse.ShowtimeInfo(
                        s.getId(),
                        s.getStartTime(),
                        s.getFormat(),
                        s.getRoom().getName(),
                        s.getBasePrice()
                ))
                .collect(Collectors.toList());

        MovieShowtimeResponse response = new MovieShowtimeResponse();
        response.setMovie(movie);
        response.setRoomType(roomType);
        response.setShowtimes(infoList);

        return response;
    }
}