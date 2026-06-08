package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.entity.MovieShowDate;
import com.example.cinema_booking_backend.repositories.MovieShowDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovieShowDateService {

    @Autowired
    private MovieShowDateRepository movieShowDateRepository;

    public List<MovieShowDate> getAllShowDates() {
        try {
            return movieShowDateRepository.findAll(Sort.by(Sort.Direction.ASC, "showDate"));
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching show dates: " + e.getMessage());
        }
    }
}