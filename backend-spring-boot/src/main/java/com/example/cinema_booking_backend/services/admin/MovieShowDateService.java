package com.example.cinema_booking_backend.services.admin;

import com.example.cinema_booking_backend.models.MovieShowDate;
import com.example.cinema_booking_backend.repositories.admin.MovieShowDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MovieShowDateService {

    @Autowired
    private MovieShowDateRepository movieShowDateRepository;

    // 1. Create a new show date
    @Transactional(rollbackFor = Exception.class)
    public MovieShowDate createShowDate(MovieShowDate showDate) {
        try {
            return movieShowDateRepository.save(showDate);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating show date: " + e.getMessage());
        }
    }

    // 2. Get all show dates
    public List<MovieShowDate> getAllShowDates() {
        try {
            return movieShowDateRepository.findAll(Sort.by(Sort.Direction.ASC, "showDate"));
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching show dates: " + e.getMessage());
        }
    }

    // 3. Update show date by ID
    @Transactional(rollbackFor = Exception.class)
    public MovieShowDate updateShowDate(UUID id, MovieShowDate showDateDetails) {
        MovieShowDate existingDate = movieShowDateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show date not found with id: " + id));

        movieShowDateRepository.findByShowDate(showDateDetails.getShowDate())
                .ifPresent(foundDate -> {
                    if (!foundDate.getId().equals(id)) {
                        throw new RuntimeException("The show date '" + showDateDetails.getShowDate() + "' already exists in the system.");
                    }
                });

        // Update information
        existingDate.setShowDate(showDateDetails.getShowDate());

        return movieShowDateRepository.save(existingDate);
    }

    // 4. Delete show date by ID
    @Transactional(rollbackFor = Exception.class)
    public void deleteShowDate(UUID id) {
        if (!movieShowDateRepository.existsById(id)) {
            throw new RuntimeException("Show date not found with id: " + id);
        }
        movieShowDateRepository.deleteById(id);
    }

    // 5. Delete all show dates
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllShowDates() {
        try {
            movieShowDateRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting all show dates: " + e.getMessage());
        }
    }
}