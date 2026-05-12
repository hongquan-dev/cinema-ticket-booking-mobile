package com.example.cinema_booking_backend.dtos.auth;
import com.example.cinema_booking_backend.enums.auth.Role;

public class LogInResponse {
    public String message;
    public String accessToken;
    public String refreshToken;
    public Role role;
    public UserResponse data;
}
