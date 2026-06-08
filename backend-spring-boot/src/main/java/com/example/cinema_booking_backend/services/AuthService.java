package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.exceptions.AuthException;
import com.example.cinema_booking_backend.entity.User;
import com.example.cinema_booking_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User signUp(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

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
