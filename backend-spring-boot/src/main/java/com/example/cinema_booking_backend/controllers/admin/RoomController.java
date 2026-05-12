package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.dtos.room.SeatResponse;
import com.example.cinema_booking_backend.models.Room;
import com.example.cinema_booking_backend.models.Seat;
import com.example.cinema_booking_backend.services.admin.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Create room for a specific cinema
    @PostMapping("/cinema/{cinemaId}")
    public ResponseEntity<Map<String, Object>> createRoom(@PathVariable UUID cinemaId, @RequestBody Room room) {
        Map<String, Object> response = new HashMap<>();
        try {
            Room savedRoom = roomService.createRoom(cinemaId, room);
            response.put("message", "Room created successfully!");
            response.put("data", savedRoom);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Get rooms by cinema ID
    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<Map<String, Object>> getRooms(
            @PathVariable UUID cinemaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Room> roomPage = roomService.getRoomsByCinema(cinemaId, page, size, search);
            if (roomPage.isEmpty()) {
                response.put("message", "No rooms found");
            } else {
                response.put("message", "Fetched rooms successfully");
            }

            response.put("rooms", roomPage.getContent());
            response.put("currentPage", roomPage.getNumber() + 1);
            response.put("totalItems", roomPage.getTotalElements());
            response.put("totalPages", roomPage.getTotalPages());

            try {
                response.put("pageSize", roomPage.getSize());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Room room = roomService.getRoomById(id);
            response.put("message", "Fetched room details successfully");
            response.put("data", room);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable UUID id, @RequestBody Room roomDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            Room updatedRoom = roomService.updateRoom(id, roomDetails);
            response.put("message", "Room updated successfully!");
            response.put("data", updatedRoom);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            roomService.deleteRoom(id);
            response.put("message", "Room deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/{id}/generate-seats")
    public ResponseEntity<Map<String, Object>> generateSeats(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            roomService.generateSeatsForRoom(id);
            response.put("message", "Seats generated successfully for this room!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<Map<String, Object>> getRoomSeats(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SeatResponse> seats = roomService.getSeatsByRoomId(id);
            response.put("message", "Fetched room seats successfully");
            response.put("data", seats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/seats/bulk-update")
    public ResponseEntity<Map<String, Object>> updateSeats(@RequestBody List<Seat> seats) {
        Map<String, Object> response = new HashMap<>();
        try {
            roomService.updateSeats(seats);
            response.put("message", "Cập nhật sơ đồ ghế thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}