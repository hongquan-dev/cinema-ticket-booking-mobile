package com.example.cinema_booking_backend.services.admin;

import com.example.cinema_booking_backend.dtos.room.SeatResponse;
import com.example.cinema_booking_backend.models.Cinema;
import com.example.cinema_booking_backend.models.Room;
import com.example.cinema_booking_backend.models.Seat;
import com.example.cinema_booking_backend.repositories.admin.CinemaRepository;
import com.example.cinema_booking_backend.repositories.admin.RoomRepository;
import com.example.cinema_booking_backend.repositories.admin.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Transactional(rollbackFor = Exception.class)
    public Room createRoom(UUID cinemaId, Room room) {
        // 1. Validate input dimensions
        validateRoomDimensions(room.getRowsCount(), room.getColsCount());

        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new RuntimeException("Cinema not found with id: " + cinemaId));

        // 2. Assign cinema and save the room first to get an ID
        room.setCinema(cinema);
        Room savedRoom = roomRepository.save(room);

        // 3. Automatically generate seats for the new room
        generateSeatsForRoom(savedRoom.getId());

        return savedRoom;
    }

    private void validateRoomDimensions(Integer rows, Integer cols) {
        if (rows == null || cols == null) {
            throw new IllegalArgumentException("Rows count and Columns count must not be null");
        }

        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Minimum rows and columns must be at least 1");
        }
        if (rows > 10) {
            throw new IllegalArgumentException("Maximum rows allowed is 10");
        }
        if (cols > 20) {
            throw new IllegalArgumentException("Maximum columns allowed is 20");
        }
    }


    public Page<Room> getRoomsByCinema(UUID cinemaId, int page, int size, String search) {
        try {
            // Validation
            if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
            if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

            int internalPage = page - 1;
            Pageable pageable = PageRequest.of(internalPage, size, Sort.by("createdAt").descending());

            if (search != null && !search.trim().isEmpty()) {
                return roomRepository.findByCinemaIdAndNameContainingIgnoreCase(cinemaId, search.trim(), pageable);
            } else {
                return roomRepository.findByCinemaId(cinemaId, pageable);
            }
        } catch (Exception e) {
            throw new RuntimeException("Database error while retrieving rooms by cinema ID: " + e.getMessage());
        }
    }

    public Room getRoomById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    @Transactional(rollbackFor = Exception.class)
    public Room updateRoom(UUID id, Room roomDetails) {
        // 1. Validate new dimensions first
        validateRoomDimensions(roomDetails.getRowsCount(), roomDetails.getColsCount());

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        // 2. Check if dimensions have changed
        boolean isDimensionsChanged = !existingRoom.getRowsCount().equals(roomDetails.getRowsCount())
                || !existingRoom.getColsCount().equals(roomDetails.getColsCount());

        // 3. Update room information
        existingRoom.setName(roomDetails.getName());
        existingRoom.setRoomType(roomDetails.getRoomType());
        existingRoom.setRowsCount(roomDetails.getRowsCount());
        existingRoom.setColsCount(roomDetails.getColsCount());

        Room updatedRoom = roomRepository.save(existingRoom);

        // 4. If dimensions changed, recreate the seats
        if (isDimensionsChanged) {
            /* Re-generate seats to match new grid size */
            generateSeatsForRoom(updatedRoom.getId());
        }

        return updatedRoom;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(UUID id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateSeatsForRoom(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 1. Clear existing seats
        seatRepository.deleteByRoomId(roomId);

        int totalRows = room.getRowsCount();
        int totalCols = room.getColsCount();

        // Calculate how many STANDARD rows/cols we want on EACH side (approx 25-30%)
        int rowPadding = (int) Math.floor(totalRows * 0.25);
        int colPadding = (int) Math.floor(totalCols * 0.20);

        // VIP boundaries
        int rowStartVip = rowPadding + 1;
        int rowEndVip = totalRows - rowPadding;

        int colStartVip = colPadding + 1;
        int colEndVip = totalCols - colPadding;

        for (int r = 1; r <= totalRows; r++) {
            char rowLetter = (char) ('A' + r - 1);

            for (int c = 1; c <= totalCols; c++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setRowIndex(r);
                seat.setColIndex(c);
                seat.setSeatNumber(String.valueOf(rowLetter) + c);
                seat.setIsActive(true);

                // Check if current position is within the symmetric VIP box
                boolean isVipRow = (r >= rowStartVip && r <= rowEndVip);
                boolean isVipCol = (c >= colStartVip && c <= colEndVip);

                if (isVipRow && isVipCol) {
                    seat.setSeatType("VIP");
                    seat.setColorCode("#F59E0B");
                    seat.setExtraPrice(new java.math.BigDecimal("20000.00"));
                } else {
                    seat.setSeatType("STANDARD");
                    seat.setColorCode("#CBD5E1");
                    seat.setExtraPrice(java.math.BigDecimal.ZERO);
                }

                seatRepository.save(seat);
            }
        }
    }

    public List<SeatResponse> getSeatsByRoomId(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RuntimeException("Room not found with id: " + roomId);
        }

        return seatRepository.findByRoomIdOrderByRowIndexAscColIndexAsc(roomId)
                .stream()
                .map(SeatResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSeats(List<Seat> seats) {
        for (Seat seat : seats) {
            // Find existing seat to ensure we don't accidentally create new ones
            Seat existingSeat = seatRepository.findById(seat.getId())
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seat.getId()));

            // Update specific fields modified in UI
            existingSeat.setSeatType(seat.getSeatType());
            existingSeat.setIsActive(seat.getIsActive());
            existingSeat.setColorCode(seat.getColorCode());

            // Logic for price adjustment based on type
            if ("VIP".equals(seat.getSeatType())) {
                existingSeat.setExtraPrice(new java.math.BigDecimal("20000.00"));
            } else {
                existingSeat.setExtraPrice(java.math.BigDecimal.ZERO);
            }

            seatRepository.save(existingSeat);
        }
    }
}