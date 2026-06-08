package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.dto.auth.LogInRequest;
import com.example.cinema_booking_backend.dto.auth.LogInResponse;
import com.example.cinema_booking_backend.dto.auth.SignUpResponse;
import com.example.cinema_booking_backend.dto.auth.UserResponse;
import com.example.cinema_booking_backend.exceptions.AuthException;
import com.example.cinema_booking_backend.entity.User;
import com.example.cinema_booking_backend.repositories.UserRepository;
import com.example.cinema_booking_backend.services.AuthService;
import com.example.cinema_booking_backend.utils.JwtUtil;
import com.example.cinema_booking_backend.validations.InputValidator;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<@NonNull SignUpResponse> signUp(@RequestBody User user) {
        try {
            User savedUser = authService.signUp(user);

            UserResponse userResponse = new UserResponse();
            userResponse.id = savedUser.getId();
            userResponse.username = savedUser.getUsername();
            userResponse.email = savedUser.getEmail();
            userResponse.phoneNumber = savedUser.getPhoneNumber();

            SignUpResponse response = new SignUpResponse();
            response.message = "User registered successfully!";
            response.data = userResponse;

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            SignUpResponse errorResponse = new SignUpResponse();
            errorResponse.message = e.getMessage();
            errorResponse.data = null;

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> signIn(@RequestBody LogInRequest request) {
        if (InputValidator.isInvalid(request.username) ||
                InputValidator.isInvalid(request.password)) {

            LogInResponse res = new LogInResponse();
            res.message = "Invalid input";
            return ResponseEntity.badRequest().body(res);
        }

        try {
            User user = authService.LogIn(request.username, request.password);

            String accessToken = jwtUtil.generateAccessToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), request.rememberMe);

            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            LogInResponse response = new LogInResponse();
            response.message = "Login successful!";
            response.accessToken = accessToken;
            response.refreshToken = refreshToken;
            response.role = user.getRole();
            response.data = mapToUserResponse(user);

            return ResponseEntity.ok(response);

        } catch (AuthException e) {

            LogInResponse error = new LogInResponse();
            error.message = e.getMessage();

            return ResponseEntity
                    .status(e.getStatus())
                    .body(error);

        } catch (Exception e) {
            LogInResponse error = new LogInResponse();
            error.message = "Server error";
            return ResponseEntity.status(500).body(error);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.username = user.getUsername();
        res.email = user.getEmail();
        res.phoneNumber = user.getPhoneNumber();
        res.fullName = user.getFullName();
        return res;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(401).body("Refresh Token is missing");
        }

        try {
            User user = userRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Token not found in database"));

            String username = jwtUtil.extractUsername(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails.getUsername())) {
                String newAccessToken = jwtUtil.generateAccessToken(userDetails.getUsername());

                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                return ResponseEntity.ok(response);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return ResponseEntity.status(401).body("Refresh Token has expired. Please log in again");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }
        return ResponseEntity.status(401).body("Refresh Token expired or invalid");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setRefreshToken(null);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}