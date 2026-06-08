package com.example.cinema_booking_backend.repositories;

import com.example.cinema_booking_backend.entity.User;
import com.example.cinema_booking_backend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(User user);
}