package com.example.cinema_booking_backend.repositories.user;

import com.example.cinema_booking_backend.models.User;
import com.example.cinema_booking_backend.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(User user);
}