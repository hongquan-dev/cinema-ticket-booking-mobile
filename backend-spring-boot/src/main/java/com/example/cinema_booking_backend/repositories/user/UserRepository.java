package com.example.cinema_booking_backend.repositories.user;

import com.example.cinema_booking_backend.models.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<@NonNull User, @NonNull UUID> {
    // Check if a username already exists in the database
    boolean existsByUsername(String username);

    // Check if an email already exists in the database
    boolean existsByEmail(String email);

    // Check if a phone number already exists in the database
    boolean existsByPhoneNumber(String phoneNumber);

    // Find a user by their unique username
    Optional<User> findByUsername(String username);

    // Find a user by their stored refresh token
    Optional<User> findByRefreshToken(String refreshToken);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Find a user by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE " +
            "(CAST(:fullName AS string) IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:fullName AS string), '%'))) AND " +
            "(CAST(:email AS string) IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%'))) AND " +
            "(CAST(:phoneNumber AS string) IS NULL OR u.phoneNumber LIKE CONCAT('%', CAST(:phoneNumber AS string), '%'))")
    Page<User> findByFilters(
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            Pageable pageable
    );
}
