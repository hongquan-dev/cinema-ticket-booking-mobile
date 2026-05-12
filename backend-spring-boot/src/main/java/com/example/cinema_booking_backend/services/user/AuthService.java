package com.example.cinema_booking_backend.services.user;

import com.example.cinema_booking_backend.exceptions.AuthException;
import com.example.cinema_booking_backend.models.User;
import com.example.cinema_booking_backend.repositories.user.UserRepository;
import com.example.cinema_booking_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Sign Up
    public User signUp(User user) {
        // Check username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check phone number
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Hash password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    // Log In
    public User LogIn(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new AuthException("Username not found", HttpStatus.NOT_FOUND)
                );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Wrong password", HttpStatus.UNAUTHORIZED);
        }

        return user;
    }
}
