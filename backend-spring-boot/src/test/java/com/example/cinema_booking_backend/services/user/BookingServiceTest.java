package com.example.cinema_booking_backend.services.user;

import com.example.cinema_booking_backend.dtos.ticket.BookingRequest;
import com.example.cinema_booking_backend.dtos.ticket.SeatWithStatusResponse;
import com.example.cinema_booking_backend.models.*;
import com.example.cinema_booking_backend.repositories.admin.SeatRepository;
import com.example.cinema_booking_backend.repositories.admin.ShowtimeRepository;
import com.example.cinema_booking_backend.repositories.user.TicketRepository;
import com.example.cinema_booking_backend.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private ShowtimeRepository showtimeRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private BookingService bookingService;

    private UUID showtimeId, userId, seatId, ticketId;
    private User mockUser;
    private Showtime mockShowtime;
    private Seat mockSeat;
    private Ticket mockTicket;

    @BeforeEach
    void setUp() {
        showtimeId = UUID.randomUUID();
        userId = UUID.randomUUID();
        seatId = UUID.randomUUID();
        ticketId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(userId);

        mockShowtime = new Showtime();
        mockShowtime.setId(showtimeId);
        mockShowtime.setBasePrice(new BigDecimal("85000"));
        Room room = new Room(); room.setId(UUID.randomUUID());
        mockShowtime.setRoom(room);

        mockSeat = new Seat();
        mockSeat.setId(seatId);
        mockSeat.setSeatNumber("A1");
        mockSeat.setIsActive(true);

        mockTicket = new Ticket();
        mockTicket.setId(ticketId);
        mockTicket.setUser(mockUser);
        mockTicket.setStatus("BOOKED");
    }

    @Test
    void createBooking_Success() {
        UUID seatId2 = UUID.randomUUID();
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(showtimeId);
        request.setUserId(userId);
        request.setSeatIds(List.of(seatId, seatId2));

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(mockShowtime));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(ticketRepository.existsByShowtimeIdAndSeatIdAndStatus(any(), any(), eq("BOOKED"))).thenReturn(false);
        when(seatRepository.findById(any())).thenReturn(Optional.of(mockSeat));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        List<Ticket> result = bookingService.createBooking(request);

        assertEquals(2, result.size());
        verify(ticketRepository, times(2)).save(any());
    }

    @Test
    void createBooking_ShowtimeNotFound() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(new BookingRequest()));
    }

    @Test
    void createBooking_SeatOccupied() {
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(showtimeId);
        request.setUserId(userId);
        request.setSeatIds(List.of(seatId));

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(mockShowtime));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(ticketRepository.existsByShowtimeIdAndSeatIdAndStatus(showtimeId, seatId, "BOOKED")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(request));
        assertTrue(ex.getMessage().contains("already occupied"));
    }

    @Test
    void cancelTicket_Success() {
        when(ticketRepository.findByIdAndUserId(ticketId, userId)).thenReturn(Optional.of(mockTicket));

        bookingService.cancelTicket(ticketId, userId);

        assertEquals("CANCELLED", mockTicket.getStatus());
        verify(ticketRepository).save(mockTicket);
    }

    @Test
    void cancelTicket_SecurityViolation() {
        when(ticketRepository.findByIdAndUserId(ticketId, userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.cancelTicket(ticketId, userId));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void getTicketsPaged_Success() {
        // 1. Khởi tạo cấu trúc dữ liệu đầy đủ để tránh NullPointerException
        Movie movie = new Movie();
        movie.setMovieName("Dune");

        Cinema cinema = new Cinema();
        cinema.setName("CGV Vincom"); // Thêm dòng này

        Room room = new Room();
        room.setName("Phòng 01");
        room.setCinema(cinema); // Gắn cinema vào room

        mockShowtime.setMovie(movie);
        mockShowtime.setRoom(room); // Gắn room vào showtime

        mockTicket.setShowtime(mockShowtime);
        mockTicket.setSeat(mockSeat);
        mockTicket.setFinalPrice(new BigDecimal("85000"));
        mockTicket.setStatus("BOOKED");
        mockTicket.setBookingTime(java.time.LocalDateTime.now());

        // 2. Giả lập Page kết quả
        Page<Ticket> page = new PageImpl<>(List.of(mockTicket));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(ticketRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        // 3. Thực thi
        var result = bookingService.getTicketsByUserIdPaged(userId, 1, 10);

        // 4. Kiểm chứng
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Dune", result.getContent().get(0).getMovieName());
        assertEquals("CGV Vincom", result.getContent().get(0).getCinemaName()); // Kiểm tra tên rạp
    }

    @Test
    void getTicketsPaged_InvalidPage() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.getTicketsByUserIdPaged(userId, 0, 10));
    }

    // ==========================================
    // 4. TEST SEAT LAYOUT (SƠ ĐỒ GHẾ)
    // ==========================================
    @Test
    void getSeatsLayout_StatusCheck() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(mockShowtime));
        when(seatRepository.findByRoomId(any())).thenReturn(List.of(mockSeat));
        // Giả sử ghế này đã nằm trong danh sách đã đặt
        when(ticketRepository.findReservedSeatIdsByShowtimeId(showtimeId)).thenReturn(List.of(seatId));

        List<SeatWithStatusResponse> layout = bookingService.getSeatsLayoutByShowtime(showtimeId);

        assertTrue(layout.get(0).isReserved(), "Ghế phải ở trạng thái đã đặt (Reserved)");
    }
}