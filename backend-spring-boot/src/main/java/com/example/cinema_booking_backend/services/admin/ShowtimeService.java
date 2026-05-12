package com.example.cinema_booking_backend.services.admin;

import com.example.cinema_booking_backend.dtos.showtime.MovieShowtimeResponse;
import com.example.cinema_booking_backend.dtos.showtime.ShowtimeRequest;
import com.example.cinema_booking_backend.models.Movie;
import com.example.cinema_booking_backend.models.Room;
import com.example.cinema_booking_backend.models.Showtime;
import com.example.cinema_booking_backend.repositories.admin.CinemaRepository;
import com.example.cinema_booking_backend.repositories.admin.MovieRepository;
import com.example.cinema_booking_backend.repositories.admin.RoomRepository;
import com.example.cinema_booking_backend.repositories.admin.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private RoomRepository roomRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    // MÔN KIỂM THỬ
    @Transactional(rollbackFor = Exception.class)
    public Showtime createShowtime(ShowtimeRequest request) {
        // --- BẮT LỖI THIẾU DỮ LIỆU (TC04, TC05, TC06, TC07) ---
        if (request.getMovieId() == null) {
            throw new IllegalArgumentException("ID phim không được để trống");
        }
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("ID phòng không được để trống");
        }
        if (request.getStartTime() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được để trống");
        }
        if (request.getBasePrice() == null) {
            throw new IllegalArgumentException("Giá vé không được để trống");
        }

        // --- BẮT LỖI GIÁ VÉ (TC11, TC12, TC20) ---
        if (request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá vé phải lớn hơn 0");
        }
        if (request.getBasePrice().compareTo(new BigDecimal("500000000")) > 0) {
            throw new IllegalArgumentException("Giá vé quá cao, vui lòng kiểm tra lại");
        }

        // 1. Kiểm tra tồn tại (TC08, TC09 -> Trả về 404)
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phim với ID: " + request.getMovieId()));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phòng với ID: " + request.getRoomId()));

        // 2. Kiểm tra thời gian quá khứ (TC14)
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Không thể tạo suất chiếu cho thời gian đã trôi qua.");
        }

        // 3. Kiểm tra ngày phát hành (TC10)
        if (request.getStartTime().toLocalDate().isBefore(movie.getReleaseDate())) {
            throw new IllegalArgumentException("Không thể tạo suất chiếu. Phim khởi chiếu ngày: " + movie.getReleaseDate());
        }

        // 4. Kiểm tra định dạng (TC03)
        if (!movie.getSelectedFormats().contains(room.getRoomType())) {
            throw new IllegalArgumentException("Phim không hỗ trợ định dạng phòng: " + room.getRoomType());
        }

        // 5. Khởi tạo Entity
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setBasePrice(request.getBasePrice());
        showtime.setFormat(room.getRoomType());

        int durationWithBuffer = movie.getDuration() + 15;
        showtime.setEndTime(showtime.getStartTime().plusMinutes(durationWithBuffer));

        // 6. Kiểm tra trùng lịch (TC02, TC17)
        List<Showtime> overlaps = showtimeRepository.findOverlappingShowtimes(
                room.getId(), showtime.getStartTime(), showtime.getEndTime());

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Trùng lịch chiếu tại phòng này!");
        }

        return showtimeRepository.save(showtime);
    }

    public Page<Showtime> getShowtimesByRoom(UUID roomId, int page, int size) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").ascending());
        return showtimeRepository.findByRoomId(roomId, pageable);
    }

    public Page<Showtime> getShowtimesByCinema(UUID cinemaId, int page, int size) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("startTime").ascending().and(Sort.by("room.name").ascending()));

        return showtimeRepository.findByCinemaId(cinemaId, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteShowtime(UUID id) {
        if (!showtimeRepository.existsById(id)) {
            throw new RuntimeException("Showtime not found");
        }
        showtimeRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Showtime updateShowtime(UUID id, ShowtimeRequest request) {
        // 1. Find existing showtime
        Showtime existingShowtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));

        // 2. Fetch Movie and Room (In case they are changed)
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Ensure showtime is not before the movie's release date
        // Convert LocalDateTime to LocalDate for date-only comparison
        if (request.getStartTime().toLocalDate().isBefore(movie.getReleaseDate())) {
            throw new RuntimeException("Cannot create showtime. The movie is scheduled to be released on: " + movie.getReleaseDate());
        }

        // 3. Validate Format
        if (!movie.getSelectedFormats().contains(room.getRoomType())) {
            throw new RuntimeException("Movie does not support " + room.getRoomType());
        }

        // 4. Update basic info
        existingShowtime.setMovie(movie);
        existingShowtime.setRoom(room);
        existingShowtime.setStartTime(request.getStartTime());
        existingShowtime.setBasePrice(request.getBasePrice());
        existingShowtime.setFormat(room.getRoomType());

        // 5. Calculate new EndTime for overlap check
        int durationWithBuffer = movie.getDuration() + 20;
        LocalDateTime newEndTime = request.getStartTime().plusMinutes(durationWithBuffer);
        existingShowtime.setEndTime(newEndTime);

        // 6. Overlap Check (Excluding this showtime ID)
        List<Showtime> overlaps = showtimeRepository.findOverlappingShowtimesExcludingSelf(
                room.getId(),
                existingShowtime.getStartTime(),
                existingShowtime.getEndTime(),
                id
        );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Time conflict with another showtime in this room!");
        }

        return showtimeRepository.save(existingShowtime);
    }

    public Page<Showtime> getAllShowtimes(int page, int size, String movieName, String roomName, String cinemaName) {
        if (page < 1) page = 1;

        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("room.cinema.name").ascending()
                        .and(Sort.by("startTime").ascending()));

        return showtimeRepository.findAllWithFilters(movieName, roomName, cinemaName, pageable);
    }

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