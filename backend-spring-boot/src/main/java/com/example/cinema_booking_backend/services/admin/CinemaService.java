package com.example.cinema_booking_backend.services.admin;

import com.example.cinema_booking_backend.models.Cinema;
import com.example.cinema_booking_backend.repositories.admin.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    @Transactional(rollbackFor = Exception.class)
    public Cinema createCinema(Cinema cinema) {
        try {
            // Simply save the cinema to PostgreSQL
            return cinemaRepository.save(cinema);
        } catch (Exception e) {
            throw new RuntimeException("System error while saving cinema: " + e.getMessage());
        }
    }

    public org.springframework.data.domain.Page<Cinema> getAllCinemasPaged(int page, int size, String search) {
        try {
            // Validation
            if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
            if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

            // internalPage is 0-indexed in Spring Data
            int internalPage = page - 1;
            org.springframework.data.domain.Pageable pageable =
                    org.springframework.data.domain.PageRequest.of(internalPage, size, org.springframework.data.domain.Sort.by("createdAt").descending());

            // Logic: Search by name if search string is provided
            if (search != null && !search.trim().isEmpty()) {
                return cinemaRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
            } else {
                return cinemaRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new RuntimeException("Database error while retrieving cinemas: " + e.getMessage());
        }
    }

    public Cinema getCinemaById(UUID id) {
        // Find cinema by ID or throw exception if not found
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema not found with id: " + id));
    }

    @Transactional(rollbackFor = Exception.class)
    public Cinema updateCinema(UUID id, Cinema cinemaDetails) {
        // 1. Find existing cinema
        Cinema existingCinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema not found with id: " + id));

        // 2. Update fields from cinemaDetails
        existingCinema.setName(cinemaDetails.getName());
        existingCinema.setAddress(cinemaDetails.getAddress());
        existingCinema.setPhoneNumber(cinemaDetails.getPhoneNumber());

        // 3. Save updated entity (updatedAt will be set via @PreUpdate hook)
        return cinemaRepository.save(existingCinema);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCinema(UUID id) {
        // 1. Check if cinema exists before deleting
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No cinema found with ID: " + id));

        try {
            // 2. Delete from Database
            cinemaRepository.delete(cinema);
        } catch (Exception e) {
            throw new RuntimeException("System error when deleting cinema: " + e.getMessage());
        }
    }
}